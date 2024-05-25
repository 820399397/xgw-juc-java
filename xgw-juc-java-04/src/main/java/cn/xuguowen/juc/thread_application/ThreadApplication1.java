package cn.xuguowen.juc.thread_application;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadApplication1
 * Package: cn.xuguowen.juc.thread_application
 * Description: 应用之互斥：为了避免临界区的竞态条件发生，有多种手段可以达到目的。阻塞式的解决方案：synchronized，Lock、非阻塞式的解决方案：原子变量
 * 1.解决 cn.xuguowen.juc.sharing_issues.Test1 类中产生的多线程读写共享变量产生的线程安全问题。
 *      synchronized（对象锁） 解决方案：采用互斥的方式让同一时刻至多只有一个线程能持有【对象锁】，其它线程再想获取这个【对象锁】时就会阻塞住。这样就能保证拥有锁的线程可以安全的执行临界区内的代码，不用担心线程上下文切换。
 *                                    因为发生了上下文切换，但是拥有锁的线程并没有释放锁资源，其他线程来抢夺锁资源还是抢不到，那就再进入阻塞状态。直到拥有锁的线程释放锁资源，会通知其他再当前锁上阻塞的线程来争抢。
 *
 * 2.synchronized 实际是用对象锁保证了临界区内代码的原子性，临界区内的代码对外是不可分割的，不会被线程切换所打断。
 * 3.为了加深理解，请思考下面的问题：
 * - 如果把 synchronized(obj) 放在 for 循环的外面，如何理解？
 *      回答：在这种情况下，每个线程会在进入循环之前获得锁，并且在完成整个循环之后才释放锁。也就是说，t1 或 t2 会一次性完成所有的递增或递减操作，而不会被另一个线程打断。
 *           第一种方式，每次循环的增减操作都要获取和释放锁，这可能会导致频繁的上下文切换。第二种方式，一个线程一次性完成所有操作，锁的竞争减少，但增加了等待时间。
 *           第一种方式，由于每次增减操作都会加锁和释放锁，锁的开销会比较大，可能导致性能下降。第二种方式，锁的粒度较粗，一个线程会在持有锁的情况下执行完整个循环，减少了锁的开销，但会增加另一个线程的等待时间。
 *           总结：放在循环内：锁的粒度细，频繁加锁释放锁，适合简单、频繁的操作。放在循环外：锁的粒度粗，一次性完成操作，适合较复杂、耗时的操作。
 * - 如果 t1 synchronized(obj1) 而 t2 synchronized(obj2) 会怎样运作？
 *      回答：线程 t1 会锁定 obj1，只有当它持有 obj1 的锁时，其他线程才无法同时锁定 obj1。线程 t2 会锁定 obj2，只有当它持有 obj2 的锁时，其他线程才无法同时锁定 obj2。
 *           这种情况下，如果 obj1 和 obj2 是独立的对象，它们之间的锁不会相互影响。这样设计可以提高并发性能，因为不同的线程可以并行地操作不同的资源，而不会相互阻塞。
 *           但是本案例中不可以这样做。
 * - 如果 t1 synchronized(obj) 而 t2 没有加会怎么样？如何理解？
 *      回答：由于线程 t2 没有使用 synchronized 对对象 obj 进行锁定，它可以自由地访问对象 obj，而不会被锁限制。这意味着线程 t2 的操作可能会与线程 t1 的操作同时进行，没有线程 t1 的同步保护。
 *           这种情况下，线程 t1 的操作是线程安全的，因为它在访问共享资源时获取了锁。但线程 t2 的操作可能会与线程 t1 的操作同时进行，没有同步保护，可能会导致竞态条件或不确定的结果。
 *
 * 4.注意：在 Java 中，synchronized 关键字主要用于实现两种概念：互斥和同步。尽管它们都涉及到线程之间的协调和访问共享资源的控制，但它们的目的和使用场景有所不同，因此存在以下区别：
 *  - 互斥指的是在多线程环境下，防止多个线程同时访问共享资源，以避免竞态条件（Race Condition）产生和数据不一致问题。synchronized 关键字提供了互斥的机制，当一个线程进入同步代码块（或方法）时，它会获取对象的锁，其他线程会被阻塞，直到获取锁的线程退出同步代码块并释放锁。
 *  - 同步指的是协调多个线程的执行顺序，确保它们按照一定的顺序访问共享资源，以避免并发访问导致的错误或不一致性。synchronized 关键字也用于实现同步，通过对关键代码块进行同步，确保在同一时间只有一个线程可以访问共享资源，从而保证了线程之间的顺序执行和数据的一致性。
 * @Author 徐国文
 * @Create 2024/5/24 12:47
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadApplication1")
public class ThreadApplication1 {

    static int count = 0;
    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                // 临界区
                synchronized (lock) {
                    count++;
                }
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                // 临界区
                synchronized (lock) {
                    count--;
                }
            }
        }, "t2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        log.debug("count:{}", count);
    }
}
