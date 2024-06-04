package cn.xuguowen.juc.synchronized_principle;

import cn.xuguowen.juc.utils.ObjectAddressUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Vector;

/**
 * ClassName: BulkRebiasing
 * Package: cn.xuguowen.juc.synchronized_principle.img
 * Description:偏向锁批量重偏向：当JVM检测到某个类的实例的偏向锁频繁被撤销但没有实际竞争时，它会将这些实例重新偏向于当前持有锁的线程。这样可以减少未来的偏向锁撤销开销。
 * 当撤销偏向锁阈值超过 20 次后，jvm 会这样觉得，我是不是偏向错了呢，于是会在给这些对象加锁时重新偏向至加锁线程
 * 测试如下案例关闭偏向锁的延迟。-XX:BiasedLockingStartupDelay=0
 * @Author 徐国文
 * @Create 2024/6/4 13:11
 * @Version 1.0
 */
@Slf4j(topic = "c.BulkRebiasing")
public class BulkRebiasing {

    // 观察t1线程和t2线程分别在第20次循环的时候，输出的运行结果。即可发现批量重偏向了
    // 13:20:47 [t1] c.BulkRebiasing - 19	Mark Word (in binary): 0000000000000000000000000000000000101011111001011001100000000101
    // 13:20:47 [t2] c.BulkRebiasing - 19	Mark Word (in binary): 0000000000000000000000000000000000101011111001011001100000000101
    // 13:20:47 [t2] c.BulkRebiasing - 19	Mark Word (in binary): 0000000000000000000000000000000000101011111010011100100100000101
    // 13:20:47 [t2] c.BulkRebiasing - 19	Mark Word (in binary): 0000000000000000000000000000000000101011111010011100100100000101
    public static void main(String[] args) {
        // 存储线程1内部创建的每一个BulkRebiasing对象，待会线程2在对其这每个对象加锁，测试批量重偏向
        Vector<BulkRebiasing> list = new Vector<>();

        // 线程1：循环创建30个对象，对这每一个对象加锁，此时每个对象都是偏向于t1线程的
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                BulkRebiasing d = new BulkRebiasing();
                list.add(d);
                synchronized (d) {
                    try {
                        log.debug(i + "\t" + ObjectAddressUtils.getAddress(d));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            synchronized (list) {
                list.notify();
            }
        }, "t1");
        t1.start();

        // 线程1：循环取出30个对象，对这每一个对象加锁，此时前20个对象会撤销偏向锁，升级为轻量级锁
        // 虽然这些对象被线程t1和线程t2访问了，但是没有竞争，这时偏向了线程 T1 的对象仍有机会重新偏向 T2，重偏向会重置对象的 Thread ID
        // 什么时候偏向于t2线程呢？当撤销偏向锁阈值超过 20 次后，jvm 会这样觉得，我是不是偏向错了呢，于是会在给这些对象加锁时重新偏向至加锁线程
        Thread t2 = new Thread(() -> {
            // t2线程在这个list对象上等着
            synchronized (list) {
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("===============> ");
            for (int i = 0; i < 30; i++) {
                BulkRebiasing d = list.get(i);
                try {
                    log.debug(i + "\t" + ObjectAddressUtils.getAddress(d));
                    synchronized (d) {
                        log.debug(i + "\t" + ObjectAddressUtils.getAddress(d));
                    }
                    log.debug(i + "\t" + ObjectAddressUtils.getAddress(d));
                } catch (Exception e) {

                }
            }
        }, "t2");
        t2.start();
    }
}
