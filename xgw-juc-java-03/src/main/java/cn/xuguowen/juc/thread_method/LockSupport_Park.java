package cn.xuguowen.juc.thread_method;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 * ClassName: LockSupport_Park
 * Package: cn.xuguowen.juc.thread_method
 * Description:park()方法：用于挂起当前线程，该线程会进入阻塞状态，直到它被其他线程显式地唤醒。
 * 1.测试interrupt()方法打断park()方法的效果
 * 2.测试interrupted()方法和isInterrupted()方法的区别:前者会清除打断标记，后者不会清除打断标记。二者相同的地方是输出的打断标记结果都为true
 *
 * @Author 徐国文
 * @Create 2024/5/22 20:16
 * @Version 1.0
 */
@Slf4j(topic = "c.LockSupport_Park")
public class LockSupport_Park {

    public static void main(String[] args) {
        test3();
    }

    /**
     * 演示：interrupt()方法打断park()方法的效果
     * 需要注意的是：Thread.interrupted()会清除打断标记，所以在这之后再次调用LockSupport.park();方法还会使得线程再次处于阻塞状态
     */
    private static void test3() {
        Thread t1 = new Thread(() -> {
            log.debug("running...");
            LockSupport.park();
            log.debug("线程的打断标记：{}", Thread.interrupted());

            LockSupport.park();
        }, "t1");
        t1.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 主线程打断t1线程
        log.debug("打断t1线程");
        t1.interrupt();
    }

    /**
     * 演示：interrupt()方法打断park()方法的效果
     * 需要注意的是：Thread.currentThread().isInterrupted()不会清除打断标记，所以在这之后再次调用LockSupport.park();方法无法使得线程再次处于阻塞状态
     */
    private static void test2() {
        Thread t1 = new Thread(() -> {
            log.debug("running...");
            LockSupport.park();
            log.debug("线程的打断标记：{}", Thread.currentThread().isInterrupted());

            LockSupport.park();
        }, "t1");
        t1.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 主线程打断t1线程
        log.debug("打断t1线程");
        t1.interrupt();
    }

    /**
     * 演示：park()方法：会使当前线程处于阻塞状态，挂起当前线程
     */
    private static void test1() {
        Thread t1 = new Thread(() -> {
            log.debug("running...");
            LockSupport.park();
            log.debug("线程的打断标记：{}", Thread.currentThread().isInterrupted());
        }, "t1");
        t1.start();
    }


}
