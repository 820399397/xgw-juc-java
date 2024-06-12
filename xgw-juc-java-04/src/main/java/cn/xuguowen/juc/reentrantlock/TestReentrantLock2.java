package cn.xuguowen.juc.reentrantlock;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: TestReentrantLock1
 * Package: cn.xuguowen.juc.reentrantlock
 * Description:测试ReentrantLock的可打断。
 *
 * @Author 徐国文
 * @Create 2024/6/12 13:12
 * @Version 1.0
 */
@Slf4j(topic = "c.TestReentrantLock2")
public class TestReentrantLock2 {

    public static ReentrantLock lock = new ReentrantLock();


    public static void main(String[] args) {
        // 测试可打断
        // test1();

        // 测试不可打断，当主线程获取了锁并启动了另一个线程t1后，它尝试在等待1秒后中断t1线程。但由于ReentrantLock的lock方法不可中断，即使主线程中断了t1线程，t1线程依然会继续等待获取锁，不会受到中断的影响。
        // 因此，在这种情况下，主线程最终释放锁之后，t1线程最终会获取到锁并打印"t1获取到锁"。
        test2();

    }

    private static void test2() {
        // 测试可打断
        Thread t1 = new Thread(() -> {
            log.debug("t1开始执行");

            // lock是不可打断的
            lock.lock();
            try {
                log.debug("t1获取到锁");
            } finally {
                lock.unlock();
            }
        },"t1");


        // 主线程获取锁
        lock.lock();
        log.debug("主线程获取到锁");
        t1.start();
        try {
            Sleeper.sleep(1);
            t1.interrupt();
            log.debug("主线程打断t1");
        } finally {
            log.debug("释放了锁");
            lock.unlock();
        }
    }

    private static void test1() {
        // 测试可打断
        Thread t1 = new Thread(() -> {
            log.debug("t1开始执行");
            try {
                // 测试可打断
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                log.debug("等锁的过程中被打断");
                e.printStackTrace();
                return;
            }
            try {
                log.debug("t1获取到锁");
            } finally {
                lock.unlock();
            }
        },"t1");


        // 主线程获取锁
        lock.lock();
        log.debug("主线程获取到锁");

        t1.start();

        try {
            Sleeper.sleep(1);
            t1.interrupt();
            log.debug("主线程打断t1");
        } finally {
            lock.unlock();
        }
    }


}
