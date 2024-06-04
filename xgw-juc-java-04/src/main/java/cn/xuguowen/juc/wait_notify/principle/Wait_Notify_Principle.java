package cn.xuguowen.juc.wait_notify.principle;

/**
 * ClassName: Wait_Notify_Principle
 * Package: cn.xuguowen.juc.wait_notify.principle
 * Description:wait notify 的原理：
 * 1.wait和notify都是属于重量级锁的方法。因为调用wait会进入Monitor监视器的waitSet中，存在于Object类中。
 * 2.已经获取锁的线程，发现条件不满足，无法执行下去的时候，调用wait方法，即可进入Monitor监视器的waitSet中，释放锁资源，线程状态变为WAITING。
 * 3.竞争锁资源的线程是进入Monitor监视器的EntryList中等待，线程状态是BLOCKED。二者状态是不一样的，但是waitSet和EntryList中的线程都不占用时间片。
 *   二者虽然都处于阻塞中，但是原因不同：前者是因为自身条件不满足，主动让出锁资源，进入等待状态。后者是竞争锁资源没有竞争到而进入阻塞状态。
 * 4.进入EntryList中，正在阻塞的线程，会在Owner线程释放锁之后被唤醒，竞争锁资源。
 * 5.进入WaitSet中的线程，会在Owner线程调用notify或者notifyAll方法之后被唤醒，但唤醒后并不意味者立刻获得锁，仍需进入EntryList中等待锁资源。
 *
 *
 * @Author 徐国文
 * @Create 2024/6/4 21:11
 * @Version 1.0
 */
public class Wait_Notify_Principle {

    // 监视器锁对象
    static final Object LOCK = new Object();

    public static void main(String[] args) {
        test1();
    }

    /**
     * wait()和notify()方法都是获取了锁资源之后，才可调用的方法。如果当前线程没有获取到锁资源，就调用wait或者notify方法就会出现异常:IllegalMonitorStateException
     */
    public static void test1() {
        // notify()方法是不会抛出异常的。
        LOCK.notify();


        /*try {
            LOCK.wait();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
    }
}
