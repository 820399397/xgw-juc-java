package cn.xuguowen.juc.principle;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ClassName: ReentrantReadWriteLock
 * Package: cn.xuguowen.juc.principle
 * Description:读写锁的原理：当读操作远远高于写操作时，这时候使用 读写锁 让 读-读 可以并发，提高性能。
 * 类似于数据库中的 select ... from ... lock in share mode
 * 总结一句话：读读可以并发。读写、写写 都是互斥的。
 *
 * 1.在多线程编程中，读写锁（也称为共享锁和排他锁）是一种常见的同步机制，允许多个线程同时读取共享资源，但在写入共享资源时，必须确保没有其他线程正在读取或写入。
 *  - 读锁（shared lock）：允许多个线程同时获取读锁，这样它们可以同时读取数据，不会互相阻塞。
 *  - 写锁（exclusive lock）：写锁是排他性的，只有一个线程可以持有写锁，并且在写锁持有期间，其他线程不能获取读锁或写锁。
 * 2.条件变量是另一种同步原语，通常与互斥锁（如 std::mutex）一起使用。它允许线程等待某个条件变为真，并在条件变为真时唤醒等待的线程。常见的使用模式是：
 *  - 一个线程在条件变量上等待（例如 wait()），直到某个条件满足。
 *  - 另一个线程改变条件并通知等待的线程（例如 notify() 或 notify_all()）。
 *
 * 注意事项：
 * 1.读锁不支持条件变量。
 *  条件变量要求等待线程在等待期间释放它持有的锁，以便其他线程能够修改条件并通知等待的线程。条件变量的等待和唤醒机制通常依赖于锁的释放和重新获取。
 *  然而，对于读锁来说，允许多个线程同时持有锁。由于读锁允许并发访问，它并没有提供将锁独占给单个线程的机制。因此：读锁无法与条件变量一起使用，因为条件变量的等待操作通常需要独占访问资源，以防止其他线程在条件变为真之前更改资源状态。读锁的共享性使得这种独占性无法保证。
 * 2.重入时升级不支持：即持有读锁的情况下去获取写锁，会导致获取写锁永久等待。
 *  锁升级不被支持的原因主要是为了防止死锁：死锁风险：如果系统允许锁升级，那么持有读锁的线程尝试获取写锁时，其他已经持有读锁的线程也可能在等待写锁。这就可能导致一种情况：所有线程都持有读锁，并且都在等待写锁，而写锁无法获得，因为没有线程愿意释放自己的读锁。这种情况会导致死锁。
 *  设计原则：为了避免这种复杂性和死锁风险，大多数读写锁的实现都禁止锁升级。这意味着如果一个线程持有读锁，它无法再获取写锁，除非它首先释放读锁。
 * 3.重入时降级支持：即持有写锁的情况下去获取读锁。
 *  3.1锁降级是指一个线程在持有写锁的情况下，获取读锁，然后释放写锁，使得线程最终只持有读锁。这样做的好处是，在完成写操作之后，如果线程仍然需要读取数据，它可以继续持有读锁而不必释放锁，允许其他线程也读取数据。
 *  3.2锁降级的过程通常分为以下几步：
 *      - 获取写锁：线程首先获取写锁，以便安全地修改共享资源。
 *      - 获取读锁：在持有写锁的情况下，线程获取读锁。
 *      - 释放写锁：获取读锁后，线程释放写锁，使得其他读线程可以继续读取数据。
 *  3.3锁降级在以下场景中非常有用：
 *      - 读后写、写后读：当一个线程需要先读取数据，再进行写操作，最后继续读取数据时，锁降级可以确保写操作期间的独占性，并在写操作后提升读取操作的并发性。
 *      - 更新缓存：例如，在更新共享缓存时，线程首先获取写锁以进行更新操作，然后获取读锁继续读取操作，并释放写锁，让其他线程也能读取更新后的缓存数据。@see cn.xuguowen.juc.principle.CachedData
 * @Author 徐国文
 * @Create 2024/8/12 12:13
 * @Version 1.0
 */
@Slf4j(topic = "c.ReentrantReadWriteLockPrinciple")
public class ReentrantReadWriteLockPrinciple {
    public static void main(String[] args) throws InterruptedException {
        DataContainer dataContainer = new DataContainer();

        // testDoubleRead(dataContainer);

        // testReadWrite(dataContainer);

        testDoubleWrite(dataContainer);
    }

    /**
     * 12:30:40 [t1] c.DataContainer - 获取写锁...
     * 12:30:40 [t1] c.DataContainer - 写入数据……
     * 12:30:40 [t2] c.DataContainer - 获取写锁...
     * 12:30:41 [t1] c.DataContainer - 释放写锁...
     * 12:30:41 [t2] c.DataContainer - 写入数据……
     * 12:30:42 [t2] c.DataContainer - 释放写锁...
     * 通过观察控制台的打印发现：t2线程的写入数据操作必须要等待t1线程的释放锁之后才可写入。产生了互斥效果
     *
     * @param dataContainer
     * @throws InterruptedException
     */
    private static void testDoubleWrite(DataContainer dataContainer) throws InterruptedException {
        // 测试两个线程都是写的操作。产生互斥的效果
        new Thread(() -> dataContainer.write(1), "t1").start();
        Thread.sleep(100);
        new Thread(() -> dataContainer.write(1), "t2").start();
    }

    /**
     * 12:26:03 [t1] c.DataContainer - 获取读锁...
     * 12:26:03 [t1] c.DataContainer - 读取数据……
     * 12:26:04 [t2] c.DataContainer - 获取写锁...
     * 12:26:04 [t1] c.DataContainer - 释放读锁...
     * 12:26:04 [t2] c.DataContainer - 写入数据……
     * 12:26:04 [t2] c.DataContainer - 释放写锁...
     * 通过观察控制台的打印发现：写锁写入数据的行为必须要等待读锁释放之后才可以写入数据，产生了互斥的效果
     *
     * @param dataContainer
     * @throws InterruptedException
     */
    private static void testReadWrite(DataContainer dataContainer) throws InterruptedException {
        // 测试一个线程读取，一个线程写入的操作.读写是互斥的
        new Thread(() -> dataContainer.read(), "t1").start();
        Thread.sleep(100);
        new Thread(() -> dataContainer.write(1), "t2").start();
    }


    private static void testDoubleRead(DataContainer dataContainer) {
        // 测试两个线程的读取操作是否存在互斥效果
        new Thread(dataContainer::read, "t1").start();
        new Thread(dataContainer::read, "t2").start();
    }
}

@Slf4j(topic = "c.DataContainer")
class DataContainer {
    private Object data;

    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    // 读锁
    private ReentrantReadWriteLock.ReadLock r = rwl.readLock();
    // 写锁
    private ReentrantReadWriteLock.WriteLock w = rwl.writeLock();


    /**
     * 读操作,加入读锁
     *
     * @return
     */
    public Object read() {
        log.debug("获取读锁...");
        r.lock();
        try {
            log.debug("读取数据……");
            Sleeper.sleep(1);
            return data;
        } finally {
            log.debug("释放读锁...");
            r.unlock();
        }
    }

    /**
     * 写操作,加入写锁
     *
     * @param newData
     */
    public void write(Object newData) {
        log.debug("获取写锁...");
        w.lock();
        try {
            log.debug("写入数据……");
            Sleeper.sleep(1);
            data = newData;
        } finally {
            log.debug("释放写锁...");
            w.unlock();
        }

    }
}

