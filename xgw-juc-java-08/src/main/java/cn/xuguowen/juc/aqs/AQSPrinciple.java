package cn.xuguowen.juc.aqs;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: AQSPrinciple
 * Package: cn.xuguowen.juc.aqs
 * Description:AQS的实现原理：全称是 AbstractQueuedSynchronizer，是阻塞式锁和相关的同步器工具的框架
 * 1.特点：
 *  - 用 state 属性来表示资源的状态（分独占模式和共享模式），子类需要定义如何维护这个状态，控制如何获取锁和释放锁
 *      - getState - 获取 state 状态
 *      - setState - 设置 state 状态
 *      - compareAndSetState - cas 机制设置 state 状态
 *      - 独占模式是只有一个线程能够访问资源，而共享模式可以允许多个线程访问资源
 *  - 提供了基于 FIFO 的等待队列，类似于 Monitor 的 EntryList
 *  - 条件变量来实现等待、唤醒机制，支持多个条件变量，类似于 Monitor 的 WaitSet
 *
 * 2.子类主要实现这样一些方法（默认抛出 UnsupportedOperationException）
 *  - tryAcquire
 *  - tryRelease
 *  - tryAcquireShared
 *  - tryReleaseShared
 *  - isHeldExclusively
 *
 *
 * @Author 徐国文
 * @Create 2024/7/31 12:07
 * @Version 1.0
 */
@Slf4j(topic = "c.AQSPrinciple")
public class AQSPrinciple {

    public static void main(String[] args) {
        MyLock lock = new MyLock();

        // 测试正常的加锁
        // testLock(lock);

        // 测试不可重入:会发现自己也会被挡住（只会打印一次 locking）
        testNotReentry(lock);
    }

    private static void testNotReentry(MyLock lock) {
        new Thread(() -> {
            lock.lock();
            log.debug("locking...");
            lock.lock();
            log.debug("locking...");
            try {
                log.debug("locking...");
                Sleeper.sleep(1);
            } finally {
                log.debug("unlocking...");
                lock.unlock();
            }
        },"t1").start();
    }


    private static void testLock(MyLock lock) {
        new Thread(() -> {
            lock.lock();
            try {
                log.debug("locking...");
                Sleeper.sleep(1);
            } finally {
                log.debug("unlocking...");
                lock.unlock();
            }
        },"t1").start();

        new Thread(() -> {
            lock.lock();
            try {
                log.debug("locking...");
            } finally {
                log.debug("unlocking...");
                lock.unlock();
            }
        },"t2").start();
    }

}

