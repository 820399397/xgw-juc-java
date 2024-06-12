package cn.xuguowen.juc.reentrantlock;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: TestReentrantLock4
 * Package: cn.xuguowen.juc.reentrantlock
 * Description:测试ReentrantLock的公平性：公平锁（Fair Lock）是指在多个线程试图获取同一把锁时，锁的获取顺序是按照线程请求锁的顺序来分配的。这样可以避免线程饥饿问题，即某些线程长期得不到锁的情况。
 * 在Java中，ReentrantLock 提供了公平锁的实现。当创建 ReentrantLock 实例时，可以通过构造函数参数来指定是否使用公平锁：
 * - 公平锁：锁会按照请求的先后顺序分配给线程。
 * - 非公平锁：锁的分配不考虑请求顺序，可能会导致某些线程长期得不到锁。
 *
 *
 * @Author 徐国文
 * @Create 2024/6/12 21:22
 * @Version 1.0
 */
@Slf4j(topic = "c.TestReentrantLock4")
public class TestReentrantLock4 {

    // 创建一个公平锁
    private static final Lock lock = new ReentrantLock(true);

    public void accessResource() {
        lock.lock();
        try {
            // 访问共享资源的代码
            System.out.println(Thread.currentThread().getName() + " got the lock");
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        TestReentrantLock4 example = new TestReentrantLock4();
        Runnable task = example::accessResource;

        // 创建并启动多个线程来测试公平锁
        for (int i = 0; i < 10; i++) {
            new Thread(task, "Thread-" + i).start();
        }


        // 1s 之后去争抢锁
        Sleeper.sleep(1);
        // 主线程强行插入
        lock.lock();
        try {
            System.out.println("Main thread got the lock");
        } finally {
            lock.unlock();
        }
    }


}
