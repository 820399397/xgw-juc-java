package cn.xuguowen.juc.synchronized_principle;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: LinghtweightLocked
 * Package: cn.xuguowen.juc.synchronized_principle
 * Description:轻量级锁的原理
 * 1.使用场景：如果一个对象虽然有多线程要加锁，但加锁的时间是错开的（也就是没有竞争），那么可以使用轻量级锁来优化。
 * 2.轻量级锁对使用者是透明的，即语法仍然是 synchronized
 * 3.轻量级锁加锁的流程：
 *  3.1 创建锁记录（Lock Record）对象，每个线程的栈帧都会包含一个锁记录的结构，内部可以存储锁定对象的Mark Word。也就是当前案例中的counter对象
 *  3.2 让锁记录中 Object reference 指向锁对象counter对象
 *  3.3 尝试用 cas 替换 LinghtweightLocked_Principle 的 Mark Word，将 Mark Word 的值存入锁记录Lock Record
 *      如果 cas 替换成功，counter对象头中存储了 锁记录地址（低62为存储地址）和状态 00（低2为存储00） ，表示由该线程给对象加锁，
 *      如果 cas 失败，有两种情况：
 *          - 如果是其它线程已经持有了该 counter对象 的轻量级锁，这时表明有竞争，进入锁膨胀过程
 *          - 如果是当前线程自己执行了 synchronized 锁重入，那么在当前线程的栈中再创建一个 锁记录 对象。这次这个锁记录对象的Lock Record为null，不再存储对象的hashcode等一些信息。增加重入计数
 *            当前线程执行完毕之后，要进行解锁的操作了，又有2种情况：
 *              - 发现Lock Record有null的锁记录，表示有重入，这时重置锁记录并减少重入计数。
 *              - 当退出 synchronized 代码块（解锁时）锁记录的值不为 null，这时使用 cas 将 Mark Word 的值恢复给对象头
 *                  cas操作又有2种情况：
 *                      - 成功，则解锁成功
 *                      - 失败，说明轻量级锁进行了锁膨胀或已经升级为重量级锁，进入重量级锁解锁流程
 * 4.锁膨胀发生的原因：首先，我们已经知道轻量级锁是通过 cas 操作 来尝试获取锁的。cas操作总共会发生2次，加锁的时候会发生1次，解锁的时候会发生1次。这2次cas操作中，如果有一次失败了，就会进入锁膨胀。
 *   锁膨胀的结果就是：升级为重量级锁。
 *   案例场景：当 Thread-1 进行轻量级加锁时，Thread-0 已经对该counter对象加了轻量级锁，这时 Thread-1 加轻量级锁失败，进入锁膨胀流程。此刻，Thread-1 要做的事情有：
 *              - 即为 counter 对象申请 Monitor 锁，让 counter 指向重量级锁地址。对象头的mark word 低62位存储的是重量级锁的地址，低2位存储的是10，表示加的是重量级锁
 *              - 然后自己进入 Monitor 的 EntryList BLOCKED
 *           当 Thread-0 退出同步块解锁时，使用 cas 将 Mark Word 的值恢复给对象头，失败(counter已经指向monitor的地址了)。这时会进入重量级解锁流程，即按照 Monitor 地址找到 Monitor 对象，设置 Owner 为 null，唤醒 EntryList 中 BLOCKED 线程
 *
 * 5.自旋优化：是一种用于减少线程在等待锁时上下文切换开销的技术。它让线程在短时间内进行忙等待（自旋），而不是立即放弃CPU资源。这可以在某些高竞争但持锁时间较短的场景中提高系统的性能。
 *           自旋会占用 CPU 资源，单核 CPU 自旋就是浪费，多核 CPU 自旋才能发挥优势。
 *           在 Java 6 之后自旋锁是自适应的，比如对象刚刚的一次自旋操作成功过，那么认为这次自旋成功的可能性会高，就多自旋几次；反之，就少自旋甚至不自旋，总之，比较智能。
 *           Java 7 之后不能控制是否开启自旋功能
 *           synchronized升级过程中会有2次自旋操作。
 *   优点：
 *      - 减少上下文切换：自旋锁避免了线程在锁竞争时进入阻塞状态，可以减少线程上下文切换的开销。
 *      - 适用于短暂持锁：在锁持有时间较短的场景中，自旋锁可以提高性能，因为等待时间可能比线程上下文切换时间短。
 *   缺点：
 *      - 浪费CPU资源：自旋锁在等待时会持续占用CPU资源，如果锁持有时间较长，会导致CPU资源的浪费。
 *      - 适用性有限：自旋锁适用于锁持有时间较短且竞争激烈的场景，对于锁持有时间较长的场景，自旋锁性能可能反而更差。
 *
 *
 * @Author 徐国文
 * @Create 2024/6/3 20:59
 * @Version 1.0
 */
@Slf4j(topic = "c.LinghtweightLocked_Principle")
public class LinghtweightLocked_Principle {

    private int count = 0;

    public int getCount() {
        return count;
    }

    public void increment() {
        synchronized (this) {
            count++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 轻量级锁案例
        // linghtweightLocked();

        // 锁膨胀场景

    }
    private static void lockExpand() throws InterruptedException {
        // 共享对象
        LinghtweightLocked_Principle counter = new LinghtweightLocked_Principle();

        int numberOfThreads = 100;
        Thread[] threads = new Thread[numberOfThreads];

        // 创建线程，模拟高并发环境，注意和轻量级锁的区别
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    counter.increment();
                }
            });
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Final count: " + counter.count);
    }

    private static void linghtweightLocked() throws InterruptedException {
        LinghtweightLocked_Principle counter = new LinghtweightLocked_Principle();
        int numberOfThreads = 100;
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            // 当多个线程同时访问 increment() 方法时，每次只有一个线程能够进入 synchronized (this) 块，这就是轻量级锁的实现。
            // 有效地防止了线程之间的竞争，从而实现了线程安全的计数器。虽然多个线程可以并发执行，但由于 synchronized 的使用，每次只有一个线程能够对 count 变量进行操作，这就是轻量级锁的核心思想。
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].join();
        }

        System.out.println("Final count: " + counter.getCount());
    }
}
