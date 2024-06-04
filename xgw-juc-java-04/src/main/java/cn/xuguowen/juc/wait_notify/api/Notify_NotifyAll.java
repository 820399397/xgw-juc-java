package cn.xuguowen.juc.wait_notify.api;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Notify_NotifyAll
 * Package: cn.xuguowen.juc.wait_notify.api
 * Description:演示notify和notifyAll方法的区别
 * 1.notify：只唤醒waitSet中的某一个线程，然后使其线程进入EntryList中，等待cpu的调度
 * 2.notifyAll：唤醒waitSet中的所有线程，然后这些线程全部进入EntryList中，等待cpu的调度
 * @Author 徐国文
 * @Create 2024/6/4 21:24
 * @Version 1.0
 */
@Slf4j(topic = "c.Notify_NotifyAll")
public class Notify_NotifyAll {
    // 锁对象
    static final Object LOCK = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("拿到锁资源");
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("时机成熟，执行其他操作");
            }
        },"t1").start();

        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("拿到锁资源");
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("时机成熟，执行其他操作");
            }
        },"t2").start();

        // 主线程睡眠2秒后，唤醒上述的线程，体会notify和notifyAll的区别
        Sleeper.sleep(2);

        // 注意:主线程也需要获取锁资源，然后执行notify方法，没有获取到锁资源执行会出现异常的。在原理中说过 @See cn.xuguowen.juc.wait_notify.principle.Wait_Notify_Principle
        synchronized (LOCK) {
            log.debug("主线程拿到锁资源");
            // 只唤醒waitSet其中的一个线程，另一个线程还在等待状态，所以程序一直处于运行中
            // LOCK.notify();

            // waitSet中的所有线程都会唤醒。
            LOCK.notifyAll();
        }

    }
}
