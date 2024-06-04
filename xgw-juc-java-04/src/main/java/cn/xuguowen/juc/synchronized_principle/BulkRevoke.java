package cn.xuguowen.juc.synchronized_principle;

import cn.xuguowen.juc.utils.ObjectAddressUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

/**
 * ClassName: BulkRevoke
 * Package: cn.xuguowen.juc.synchronized_principle
 * Description:偏向锁的批量撤销：当JVM检测到某个类的实例的偏向锁频繁被多个线程竞争时，它会批量撤销这些实例的偏向锁，并将它们升级为轻量级锁或重量级锁，从而避免频繁的偏向锁撤销操作带来的开销。
 * 当撤销偏向锁阈值超过 40 次后，jvm 会这样觉得，自己确实偏向错了，根本就不该偏向。于是整个类的所有对象都会变为不可偏向的，新建的对象也是不可偏向的。
 * 测试如下案例需要关闭偏向锁的延迟。-XX:BiasedLockingStartupDelay=0
 *
 * @Author 徐国文
 * @Create 2024/6/4 13:36
 * @Version 1.0
 */
@Slf4j(topic = "c.BulkRevoke")
public class BulkRevoke {

    static Thread t1,t2,t3;

    // 一开始都是偏向于t1线程的
    // 13:45:08 [t1] c.BulkRevoke - 19	Mark Word (in binary): 0000000000000000000000000000000000101011100011011001000000000101
    // t2线程竞争锁，前19次升级为轻量级锁，直到第20次的时候，发送了批量重偏向，这个类的对象又开始偏向于t2线程了
    // 13:45:08 [t2] c.BulkRevoke - 19	Mark Word (in binary): 0000000000000000000000000000000000101011100011011001000000000101
    // 13:45:08 [t2] c.BulkRevoke - 19	Mark Word (in binary): 0000000000000000000000000000000000101011100011011001100100000101
    // 13:45:08 [t2] c.BulkRevoke - 19	Mark Word (in binary): 0000000000000000000000000000000000101011100011011001100100000101
    // t3线程竞争锁，前19次也就是前20个对象，已经是不可偏向的了，这个不用说了。从t1到t2的时候，这前20个对象也是这样的，要升级为轻量级锁
    // 13:45:08 [t3] c.BulkRevoke - 19	Mark Word (in binary): 0000000000000000000000000000000000101011100011011001100100000101
    // 13:45:08 [t3] c.BulkRevoke - 19	Mark Word (in binary): 0000000000000000000000000000000000101011111100101111000111001000
    // 13:45:08 [t3] c.BulkRevoke - 19	Mark Word (in binary): 0000000000000000000000000000000000000000000000000000000000000001
    // t3线程：从第21个对象开始，这些对象上一次已经偏向于t2线程了，所以这里偏向锁被撤销，直到38，也就是第39个对象
    // 13:45:08 [t3] c.BulkRevoke - 38	Mark Word (in binary): 0000000000000000000000000000000000101011100011011001100100000101
    // 13:45:08 [t3] c.BulkRevoke - 38	Mark Word (in binary): 0000000000000000000000000000000000101011111100101111000111001000
    // 13:45:08 [t3] c.BulkRevoke - 38	Mark Word (in binary): 0000000000000000000000000000000000000000000000000000000000000001
    // 第40个对象，也就是主线程输出的信息：发现这个对象不可偏向了，就是处于正常状态了，以后这个类的所有对象就不能是偏向锁了
    // 13:45:08 [main] c.BulkRevoke - Mark Word (in binary): 0000000000000000000000000000000000000000000000000000000000000001
    // 如果把loopNumber的值改为38，则没有超过批量撤销的阈值，所以还是偏向锁
    // 13:57:24 [main] c.BulkRevoke - Mark Word (in binary): 0000000000000000000000000000000000000000000000000000000100000101

    public static void main(String[] args) throws Exception {
        // 存储BulkRevoke类的实力对象
        Vector<BulkRevoke> list = new Vector<>();

        // 变量的值为39,，就是循环40次。此时可以看出BulkRevoke类的对象以后不会是偏向锁了。
        // int loopNumber = 39;
        // 变量的值为39,，就是循环39次，没打到阈值。此时可以看出BulkRevoke类的对象还可以是偏向锁。
        int loopNumber = 38;


        t1 = new Thread(() -> {
            for (int i = 0; i < loopNumber; i++) {
                BulkRevoke d = new BulkRevoke();
                list.add(d);
                synchronized (d) {
                    try {
                        log.debug(i + "\t" + ObjectAddressUtils.getAddress(d));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // 唤醒t2线程
            LockSupport.unpark(t2);
        }, "t1");
        t1.start();


        t2 = new Thread(() -> {
            try {
                // 处于阻塞状态
                LockSupport.park();
                log.debug("===============> ");

                for (int i = 0; i < loopNumber; i++) {
                    BulkRevoke d = list.get(i);
                    log.debug(i + "\t" + ObjectAddressUtils.getAddress(d));
                    synchronized (d) {
                        log.debug(i + "\t" + ObjectAddressUtils.getAddress(d));
                    }
                    log.debug(i + "\t" + ObjectAddressUtils.getAddress(d));
                }

                // 唤醒t3线程
                LockSupport.unpark(t3);
            } catch (Exception e) {

            }
        }, "t2");
        t2.start();


        t3 = new Thread(() -> {
            try {
                // 处于阻塞状态
                LockSupport.park();
                log.debug("===============> ");


                for (int i = 0; i < loopNumber; i++) {
                    BulkRevoke d = list.get(i);
                    log.debug(i + "\t" + ObjectAddressUtils.getAddress(d));
                    synchronized (d) {
                        log.debug(i + "\t" + ObjectAddressUtils.getAddress(d));
                    }
                    log.debug(i + "\t" + ObjectAddressUtils.getAddress(d));
                }
            } catch (Exception e) {
            }
        }, "t3");
        t3.start();

        // t1,t2,t3线程的运行顺序：t1先运行，t2，t3处于阻塞状态。t1执行完完毕之后唤醒t2,t2执行完毕之后唤醒t3，所以t3最后执行
        t3.join();


        log.debug(ObjectAddressUtils.getAddress(new BulkRevoke()));
    }

}
