package cn.xuguowen.juc.pattern;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: Sync_SequentialControl
 * Package: cn.xuguowen.juc.pattern
 * Description:同步模式之顺序控制
 * 1.固定运行顺序
 * 2.案例场景：2个线程分别打印1 和 2 。要求必须先打印2，然后再打印1.
 *   - wait/notify实现
 * @Author 徐国文
 * @Create 2024/6/13 11:52
 * @Version 1.0
 */
@Slf4j(topic = "c.Sync_SequentialControl1")
public class Sync_SequentialControl1 {

    // 锁对象
    private final static Object lock = new Object();

    // 标记t2线程是否运行了
    private static boolean t2Runnable = false;

    private final static ReentrantLock LOCK = new ReentrantLock();

    private static Condition t2Condition = LOCK.newCondition();

    public static void main(String[] args) {
        // test1();

        // 使用wait/notify实现固定运行顺序
        // test2();

        // 使用park/unpark实现规定运行顺序
        // test3();

        // 使用ReentrantLock来实现
        test4();
    }

    /**
     * 使用ReentrantLock来实现
     */
    private static void test4() {
        Thread t1 = new Thread(() -> {
            LOCK.lock();
            try {
                while (!t2Runnable) {
                    try {
                        t2Condition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("1");
            } finally {
                LOCK.unlock();
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            LOCK.lock();
            try {
                log.debug("2");
                t2Runnable = true;
                t2Condition.signal();
            } finally {
                LOCK.unlock();
            }
        }, "t2");

        t1.start();
        t2.start();
    }

    /**
     * 使用park/unpark实现规定运行顺序
     */
    private static void test3() {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            log.debug("1");
        }, "t1");

        Thread t2 = new Thread(() -> {
            log.debug("2");
            // 如果先运行t2线程，也会提前把干粮补给给t1，到时候t1park的时候，发现有干粮，就会继续向下运行
            LockSupport.unpark(t1);
        }, "t2");

        t1.start();
        t2.start();
    }

    /**
     * 使用wait/notify实现固定运行顺序
     */
    private static void test2() {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                while (!t2Runnable) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("1");
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                log.debug("2");
                t2Runnable = true;
                lock.notify();
            }
        }, "t2");

        t1.start();
        t2.start();
    }

    /**
     * 如下程序的打印顺序是随机的。
     * 因为线程t1和t2交替执行，谁先抢到CPU执行权，谁就先执行。
     */
    private static void test1() {
        Thread t1 = new Thread(() -> {
            log.debug("1");
        }, "t1");

        Thread t2 = new Thread(() -> {
            log.debug("2");
        }, "t2");

        t1.start();
        t2.start();
    }
}
