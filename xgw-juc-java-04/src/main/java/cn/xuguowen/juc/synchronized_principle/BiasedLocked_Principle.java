package cn.xuguowen.juc.synchronized_principle;

import cn.xuguowen.juc.utils.ObjectAddressUtils;
import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: BiasedLocked_Principle
 * Package: cn.xuguowen.juc.synchronized_principle
 * Description:偏向锁的原理以及应用
 * 1.偏向锁出现的场景：轻量级锁在没有竞争时（就自己这个线程），每次重入仍然需要执行 CAS 操作。CAS操作也是耗费性能和资源的。
 *   所以Java 6 中引入了偏向锁来做进一步优化：只有第一次使用 CAS 将线程 ID 设置到对象的 Mark Word 头，之后发现这个线程 ID 是自己的就表示没有竞争，不用重新 CAS。以后只要不发生竞争，这个对象就归该线程所有
 * 2.验证偏向锁默认是开启的。那么对象创建后，markword 的低3位是 101，这时它的 thread、epoch、age 都为 0
 * 3.验证偏向锁的延迟性
 * 4.验证偏向锁的禁用
 * 5.验证调用对象的hashcode方法，会导致对象的锁状态变为轻量级锁。因为hashcode值的存储需要31位，而偏向锁的threadId要54位存在，存不下了。
 *   轻量级锁和重量级锁，锁对象的一些记录信息是在Lock Record 或者 Monitor中记录者
 * @Author 徐国文
 * @Create 2024/6/3 21:47
 * @Version 1.0
 */
@Slf4j(topic = "c.BiasedLocked_Principle")
public class BiasedLocked_Principle {

    private int count = 0;

    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) throws Exception {
        // 偏向锁的场景
        // biasedlocked();

        // 验证偏向锁是默认开启的:添加JVM参数-XX:BiasedLockingStartupDelay=0禁用延迟
        // 处于偏向锁的对象解锁后，线程 id 仍存储于对象头中
        defaultOpen();

        // 验证偏向锁的延迟性，需要删除JVM参数-XX:BiasedLockingStartupDelay=0
        // delay();

        // 验证偏向锁的禁用,需要添加JVM参数-XX:-UseBiasedLocking
        // disableBiased();

        // 验证调用对象的hashcode方法，会导致对象的锁状态变为轻量级锁
        // 从存储的角度思考为什么hashcode方法会导致没有偏向锁
        // caseStatus();

    }

    public static void caseStatus() throws Exception {
        BiasedLocked_Principle test = new BiasedLocked_Principle();
        // 0000000000000000000000000000000000000000000000000000000000000001
        log.info("对象创建前：{}",ObjectAddressUtils.getAddress(test));
        // 第一次调用hashcode方法，会为该对象生成hashcode值，占31位
        test.hashCode();

        synchronized (test) {
            // 0000000000000000000000000000000000000010110001001111010100111000
            log.info("对象加锁后：{}",ObjectAddressUtils.getAddress(test));
        }

        // 0000000000000000000000000001001010111100011010000111010000000001
        log.info("解锁后：{}",ObjectAddressUtils.getAddress(test));

    }

    /**
     * 禁用偏向锁，需要添加JVM参数-XX:-UseBiasedLocking
     */
    private static void disableBiased() throws Exception {
        BiasedLocked_Principle test = new BiasedLocked_Principle();
        // 0000000000000000000000000000000000000000000000000000000000000001
        log.info("对象创建前：{}",ObjectAddressUtils.getAddress(test));

        synchronized (test) {
            // 注意地址的低2为00，其实表明加的就是轻量级锁，低62为表示轻量级锁的lock record的地址 0000000000000000000000000000000000000010111000001111001100101000
            log.info("对象加锁后：{}",ObjectAddressUtils.getAddress(test));
        }

        // 0000000000000000000000000000000000000000000000000000000000000001
        log.info("解锁后：{}",ObjectAddressUtils.getAddress(test));
    }

    /**
     * 测试偏向锁的延迟
     * @throws Exception
     */
    private static void delay() throws Exception {
        BiasedLocked_Principle test = new BiasedLocked_Principle();
        // 0000000000000000000000000000000000000000000000000000000000000001
        log.info("对象创建前：{}",ObjectAddressUtils.getAddress(test));

        Sleeper.sleep(5);
        // 再次创建对象，看这个对象的偏向标记为  0000000000000000000000000000000000000000000000000000000000000101
        BiasedLocked_Principle test1 = new BiasedLocked_Principle();
        log.info("休眠4s后，再次打印对象头的信息：{}",ObjectAddressUtils.getAddress(test1));
    }

    /**
     * 验证偏向锁是默认开启的:添加JVM参数-XX:BiasedLockingStartupDelay=0禁用延迟
     * 处于偏向锁的对象解锁后，线程 id 仍存储于对象头中
     *
     * @throws Exception
     */
    private static void defaultOpen() throws Exception {
        BiasedLocked_Principle test = new BiasedLocked_Principle();
        // 0000000000000000000000000000000000000000000000000000000000000 101
        log.info(ObjectAddressUtils.getAddress(test));

        synchronized (test) {
            // 0000000000000000000000000000000000000010110110000011100000000 101
            log.info("对象加锁后：{}",ObjectAddressUtils.getAddress(test));
        }
        // 处于偏向锁的对象解锁后，线程 id 仍存储于对象头中：0000000000000000000000000000000000000010110110000011100000000 101
        log.info("解锁后：{}",ObjectAddressUtils.getAddress(test));
    }

    /**
     * 验证偏向锁的使用场景
     */
    private static void biasedlocked() {
        BiasedLocked_Principle counter = new BiasedLocked_Principle();

        // 单线程多次进入同步块
        // Counter类的increment方法是同步的。当thread线程第一次进入increment方法时，锁会偏向该线程。后续该线程进入同步块时，无需执行锁操作，因为锁已经偏向了它。
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
            System.out.println("Final count: " + counter.getCount());
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
