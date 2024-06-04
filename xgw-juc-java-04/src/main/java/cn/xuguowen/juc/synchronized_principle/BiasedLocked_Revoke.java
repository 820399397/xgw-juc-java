package cn.xuguowen.juc.synchronized_principle;

import cn.xuguowen.juc.utils.ObjectAddressUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: BiasedLocked_Revoke
 * Package: cn.xuguowen.juc.synchronized_principle
 * Description:偏向锁的撤销
 * 1.调用对象的hashcode()方法，会撤销偏向锁
 * 2.其他线程使用锁对象，会将偏向锁升级为轻量级锁
 * 3.调用 wait/notify，会撤销偏向锁
 * @Author 徐国文
 * @Create 2024/6/4 12:12
 * @Version 1.0
 */
@Slf4j(topic = "c.BiasedLocked_Revoke")
public class BiasedLocked_Revoke {

    public static void main(String[] args) throws Exception {
        // revoke1();

        // revoke2();

        revoke3();
    }

    /**
     * 调用 wait/notify，会撤销偏向锁，升级为重量级锁
     */
    private static void revoke3() {
        BiasedLocked_Revoke revoke = new BiasedLocked_Revoke();

        Thread t1 = new Thread(() -> {
            try {
                // 0000000000000000000000000000000000000000000000000000000000000101
                log.info("对象加锁前：{}", ObjectAddressUtils.getAddress(revoke));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            synchronized (revoke) {
                try {
                    // 0000000000000000000000000000000000101011100001100101000000000101
                    log.info("对象加锁后：{}", ObjectAddressUtils.getAddress(revoke));

                    revoke.wait();

                    // 0000000000000000000000000000000000100110010011101010010100101010
                    log.info("被唤醒后：{}", ObjectAddressUtils.getAddress(revoke));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1");
        t1.start();

        new Thread(() -> {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (revoke) {
                log.debug("notify");
                revoke.notify();
            }
        }, "t2").start();

    }

    /**
     * 其他线程使用锁对象，会将偏向锁升级为轻量级锁
     * @throws Exception
     */
    private static void revoke2() throws Exception {
        BiasedLocked_Revoke revoke = new BiasedLocked_Revoke();

        Thread t1 = new Thread(() -> {
            try {
                // 0000000000000000000000000000000000000000000000000000000000000101
                log.info("对象加锁前：{}", ObjectAddressUtils.getAddress(revoke));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            synchronized (revoke) {
                try {
                    // 0000000000000000000000000000000000101011001111110010100000000101
                    log.info("对象加锁后：{}", ObjectAddressUtils.getAddress(revoke));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                // 0000000000000000000000000000000000101011001111110010100000000101
                log.info("释放锁资源后：{}", ObjectAddressUtils.getAddress(revoke));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            synchronized (BiasedLocked_Revoke.class) {
                BiasedLocked_Revoke.class.notify();
            }

        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            // 一开始执行就让其处于阻塞状态，等待t1线程执行完毕之后，再去执行。因为这样才能测试出偏向锁的撤销
            synchronized (BiasedLocked_Revoke.class) {
                try {
                    BiasedLocked_Revoke.class.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


            try {
                // 0000000000000000000000000000000000101011001111110010100000000101
                log.info("对象加锁前：{}", ObjectAddressUtils.getAddress(revoke));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            synchronized (revoke) {
                try {
                    // 0000000000000000000000000000000000101011101011111111001000100000
                    log.info("对象加锁后：{}", ObjectAddressUtils.getAddress(revoke));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                // 0000000000000000000000000000000000000000000000000000000000000001
                log.info("释放锁资源后：{}", ObjectAddressUtils.getAddress(revoke));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "t2");
        t2.start();
    }

    /**
     * 调用对象的hashcode()方法，会撤销偏向锁
     * 偏向锁有延迟，所以想要测试如下的方法，需要在运行的时候添加JVM参数：-XX:BiasedLockingStartupDelay=0
     *
     * 轻量级锁会在锁记录中记录 hashCode
     * 重量级锁会在 Monitor 中记录 hashCode
     */
    private static void revoke1() throws Exception {
        BiasedLocked_Revoke revoke = new BiasedLocked_Revoke();
        // 0000000000000000000000000000000000000000000000000000000000000101
        log.info("对象加锁前：{}", ObjectAddressUtils.getAddress(revoke));
        // 调用对象的hashcode方法。当打开下面的代码，可以看到偏向锁被撤销了
        revoke.hashCode();

        // 0000000000000000000000000000000000000011000001001111011011001000
        synchronized (revoke) {
            log.info("对象加锁后：{}", ObjectAddressUtils.getAddress(revoke));
        }

        // 0000000000000000000000000001001010111100011010000111010000000001
        log.info("释放锁资源后：{}", ObjectAddressUtils.getAddress(revoke));
    }

}
