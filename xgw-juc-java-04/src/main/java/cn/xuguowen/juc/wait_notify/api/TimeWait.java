package cn.xuguowen.juc.wait_notify.api;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: TimeWait
 * Package: cn.xuguowen.juc.wait_notify.api
 * Description:带时限的wait方法
 *
 * @Author 徐国文
 * @Create 2024/6/4 21:33
 * @Version 1.0
 */
@Slf4j(topic = "c.TimeWait")
public class TimeWait {

    // 锁资源
    static final Object LOCK = new Object();

    public static void main(String[] args) {

        // test1();
        // test2();

        // test3();

        test4();
    }

    /**
     * 测试带时限的wait方法，传入2个参数的
     * nanos纳秒其实是不起作用的。可以看下源码
     * if (nanos > 0) {
     *     timeout++;
     * }
     * 也就等待时间加1ms
     */
    private static void test4() {
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("获取锁");
                try {
                    // 只等待1s中，1s过后，继续下面的执行
                    LOCK.wait(2000,6666);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("时机成熟，执行其他操作");
            }
        },"t1").start();
    }

    /**
     * 带时限的wait方法，当发现其他线程提前唤醒自己，则不在等待。
     * 如果其他线程在规定的时间内没有唤醒自己，自己则会继续运行，因为等待时间已经过了。
     */
    private static void test3() {
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("获取锁");
                try {
                    // 只等待1s中，1s过后，继续下面的执行
                    LOCK.wait(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("时机成熟，执行其他操作");
            }
        },"t1").start();

        Sleeper.sleep(3);

        synchronized (LOCK) {
            log.debug("主线程获取锁，提前唤醒t1线程");
            LOCK.notifyAll();
        }
    }

    /**
     *  测试wait带时限的用法。只等待Ns，如果过了，则不等待，继续执行。
     */
    private static void test2() {
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("获取锁");
                try {
                    // 只等待1s中，1s过后，继续下面的执行
                    LOCK.wait(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("时机成熟，执行其他操作");
            }
        },"t1").start();
    }

    /**
     * 测试wait没有时限的方法。
     * 其实底层也是基于带时限的wait方法实现的，传入的参数是0，表示一直处于等待。
     * wait(0);
     */
    private static void test1() {
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("获取锁");
                try {
                    // 处于一直等待中，没人唤醒则一直在waitSet中
                    LOCK.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("时机成熟，执行其他操作");
            }
        },"t1").start();
    }
}
