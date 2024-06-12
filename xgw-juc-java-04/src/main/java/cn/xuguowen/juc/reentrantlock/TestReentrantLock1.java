package cn.xuguowen.juc.reentrantlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: TestReentrantLock1
 * Package: cn.xuguowen.juc.reentrantlock
 * Description:测试ReentrantLock的可重入性。
 * 1.ReentrantLock 和 synchronized 都是用来实现线程同步的机制，但 ReentrantLock 提供了更多的灵活性和功能。在某些场景下，使用 ReentrantLock 可能更合适。
 * 2.相对于 synchronized 它具备如下特点
 *  - 可中断：可以中断正在等待的线程。这个特点可以避免死锁的发生
 *  - 可以设置超时时间：可以尝试获取锁并在指定时间内失败
 *  - 可以设置为公平锁，按获取锁的顺序执行，避免线程饥饿
 *  - 多条件变量：可以与多个条件变量一起使用，以便更复杂的同步。
 * 3.sunchronized 具备如下特点
 *  - 简单易用：使用方便，只需在方法或代码块上添加 synchronized 关键字。
 *  - 隐式锁定：不需要显式获取和释放锁。
 *  - 不可中断：线程在等待锁时不能被中断。
 * 4.二者相同的地方是：都支持可重入。可重入是指同一个线程如果首次获得了这把锁，那么因为它是这把锁的拥有者，因此有权利再次获取这把锁。如果是不可重入锁，那么第二次获得锁时，自己也会被锁挡住
 *
 * @Author 徐国文
 * @Create 2024/6/12 13:12
 * @Version 1.0
 */
@Slf4j(topic = "c.TestReentrantLock1")
public class TestReentrantLock1 {

    public static ReentrantLock lock = new ReentrantLock();

    /**
     * 测试ReentrantLock的可重入性
     * @param args
     */
    public static void main(String[] args) {
        // 测试可重入
        test1();
    }

    public static void test1() {
        // 尝试获取锁
        lock.lock();
        try {
            log.debug("entrn main");
            m1();
        } finally {
            // 释放锁
            lock.unlock();
        }
    }

    private static void m1() {
        lock.lock();
        try {
            log.debug("entrn m1");
            m2();
        } finally {
            lock.unlock();
        }
    }

    private static void m2() {
        lock.lock();
        try {
            log.debug("entrn m2");
        } finally {
            lock.unlock();
        }
    }
}
