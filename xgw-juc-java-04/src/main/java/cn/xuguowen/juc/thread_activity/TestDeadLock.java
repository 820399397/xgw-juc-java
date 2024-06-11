package cn.xuguowen.juc.thread_activity;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: TestDeadLock
 * Package: cn.xuguowen.juc.thread_activity
 * Description:活跃性问题之死锁：当两个或多个线程互相等待对方持有的资源时，会导致所有线程都无法继续执行，从而陷入永久等待状态。
 * 例如，线程A持有资源1，并等待资源2，而线程B持有资源2，并等待资源1，这样就导致了死锁。
 *
 * 如何定位到程序中发生死锁的线程？
 * 方式1：jstack PID 查看:jstack 12596
 * 方式2：使用jconsole可视化程序查看
 * 另外如果由于某个线程进入了死循环，导致其它线程一直等待，对于这种情况 linux 下可以通过 top 先定位到CPU 占用高的 Java 进程，再利用 top -Hp 进程id 来定位是哪个线程，最后再用 jstack 排查
 *
 * 避免死锁的策略：
 * 1.资源排序法：保证所有线程按相同的顺序获取锁。
 * 2.超时机制：设置超时，超时后释放已获得的锁，避免无限等待。
 *
 *
 * @Author 徐国文
 * @Create 2024/6/11 14:54
 * @Version 1.0
 */
@Slf4j(topic = "c.TestDeadLock")
public class TestDeadLock {

    public static final Object A = new Object();

    public static final Object B = new Object();

    // ReentrantLock用于替代sychronized，可以尝试获取锁并设置超时时间
    private static final Lock lock1 = new ReentrantLock();

    private static final Lock lock2 = new ReentrantLock();

    public static void main(String[] args) {
        //发生死锁
        // happenDeadLock();

        // 避免死锁策略1：顺序枷锁
        // sequentialLocking();

        // 避免死锁策略2：超时机制
        timeout();
    }

    /**
     * 超时机制：是在尝试获取锁时设置一个最大等待时间。如果线程在指定的时间内无法获得锁，则放弃获取锁，从而避免无限等待。
     * 这可以通过ReentrantLock类中的tryLock方法来实现，tryLock方法可以接受一个超时时间参数。
     */
    private static void timeout() {
        // 通过tryLock(long timeout, TimeUnit unit)方法来尝试获取锁。如果在指定时间内无法获取锁，就会返回false。
        // 如果tryLock成功（在指定时间内获得锁），则继续执行并尝试获取第二把锁。
        // 如果无法在指定时间内获取第二把锁，则释放已持有的锁，并记录相应的日志信息。

        Thread t1 = new Thread(() -> {
            try {
                if (lock1.tryLock(1000, TimeUnit.MILLISECONDS)) {
                    try {
                        log.debug("Thread 1: Waiting for lock 2...");
                        Thread.sleep(10); // 模拟工作
                        if (lock2.tryLock(1000, TimeUnit.MILLISECONDS)) {
                            try {
                                log.debug("Thread 1: Holding lock 1 & 2...");
                            } finally {
                                lock2.unlock();
                            }
                        } else {
                            log.debug("Thread 1: Could not acquire lock 2, releasing lock 1");
                        }
                    } finally {
                        lock1.unlock();
                    }
                } else {
                    log.debug("Thread 1: Could not acquire lock 1");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1");


        Thread t2 = new Thread(() -> {
            try {
                if (lock2.tryLock(1000, TimeUnit.MILLISECONDS)) {
                    try {
                        log.debug("Thread 2: Waiting for lock 1...");
                        Thread.sleep(10); // 模拟工作
                        if (lock1.tryLock(1000, TimeUnit.MILLISECONDS)) {
                            try {
                                log.debug("Thread 2: I have the locks");
                            } finally {
                                lock1.unlock();
                            }
                        } else {
                            log.debug("Thread 2: Could not acquire lock 1, releasing lock 2");
                        }
                    } finally {
                        lock2.unlock();
                    }
                } else {
                    log.debug("Thread 2: Could not acquire lock 2");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2");

        t1.start();
        t2.start();
    }

    /**
     * 顺序加锁
     */
    private static void sequentialLocking() {
        Thread t1 = new Thread(() -> {
            synchronized (A) {
                log.debug("lock A");
                Sleeper.sleep(1);
                synchronized (B) {
                    log.debug("lock B");
                    log.debug("操作...");
                }
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (A) {
                log.debug("lock A");
                Sleeper.sleep(0.5);
                synchronized (B) {
                    log.debug("lock B");
                    log.debug("操作...");
                }
            }
        }, "t2");

        t1.start();
        t2.start();
    }


    /**
     * 发生死锁
     */
    private static void happenDeadLock() {
        Thread t1 = new Thread(() -> {
            synchronized (A) {
                log.debug("lock A");
                Sleeper.sleep(1);
                synchronized (B) {
                    log.debug("lock B");
                    log.debug("操作...");
                }
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (B) {
                log.debug("lock B");
                Sleeper.sleep(0.5);
                synchronized (A) {
                    log.debug("lock A");
                    log.debug("操作...");
                }
            }
        }, "t2");

        t1.start();
        t2.start();
    }
}
