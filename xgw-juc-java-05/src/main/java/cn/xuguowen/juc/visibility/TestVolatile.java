package cn.xuguowen.juc.visibility;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: TestVoliate
 * Package: cn.xuguowen.visibility
 * Description:可见性问题的引出以及如何解决这个问题。
 * 可见性：它保证的是在多个线程之间，一个线程对 volatile 变量的修改对另一个线程可见， 不能保证原子性，仅用在一个写线程，多个读线程的情况。
 *
 *
 * @Author 徐国文
 * @Create 2024/6/18 12:17
 * @Version 1.0
 */
@Slf4j(topic = "c.TestVolatile")
public class TestVolatile {


    public static void main(String[] args) {
        // test1();

        // test2();

        // test3();

        test4();

    }

    static boolean test4Run = true;

    /**
     * 思考题：如果在前面示例的死循环中加入 System.out.println() 会发现即使不加 volatile 修饰符，线程 t 也能正确看到对 run 变量的修改了，想一想为什么？
     * 或者是日志打印log.debug();
     *
     * 原因：Java 内存模型允许线程缓存变量的副本到寄存器或者 CPU 缓存中，因此一个线程对变量的修改不一定立刻对其他线程可见。
     *      但是，当调用 System.out.println() 或 log.debug() 时，这些方法通常会执行 I/O 操作，这会导致 CPU 刷新缓存，从而使得线程读取到最新的变量值。
     *      System.out.println() 和 log.debug() 这样的 I/O 操作通常涉及底层的同步机制，例如加锁和解锁，这些操作本身会引发内存屏障（memory barrier）。
     *      内存屏障会强制线程刷新其工作内存，从主存重新读取变量，从而使得对 test4Run 变量的修改变得可见。
     *      现代 JVM 的 Just-In-Time (JIT) 编译器会对代码进行优化。在简单的循环中，JIT 可能会将变量缓存到寄存器中，导致变化不可见。
     *      但是，当有 I/O 操作时，这些优化通常会被打断，因为 I/O 操作涉及的方法不会被 JIT 编译器过度优化，从而保持了变量的可见性。
     */
    private static void test4() {
        Thread t1 = new Thread(() -> {
            while (test4Run) {
                System.out.println("t1 run...");
                // log.debug("t1 run...");
            }
        }, "t1");
        t1.start();

        // 主线程睡眠1s后，将run的值改为false。验证t1线程是否会终止运行
        Sleeper.sleep(1);
        test4Run = false;
    }

    static boolean test3Run = true;

    static final Object LOCK = new Object();

    /**
     * 也可以使用synchronized来解决这个可见性的问题.
     *  synchronized 语句块既可以保证代码块的原子性，也同时保证代码块内变量的可见性。但缺点是synchronized 是属于重量级操作，性能相对更低
     */
    private static void test3() {
        Thread t1 = new Thread(() -> {
            while (true) {
                synchronized (LOCK) {
                    if (!test3Run) {
                        break;
                    }
                }
            }
        }, "t1");
        t1.start();

        // 主线程睡眠1s后，将run的值改为false。验证t1线程是否会终止运行
        Sleeper.sleep(1);
        test3Run = false;
    }

    volatile static boolean test2Run = true;

    /**
     * 针对test1()方法中可见性问题的解决：volatile（易变关键字）
     * 它可以用来修饰成员变量和静态成员变量，他可以避免线程从自己的工作缓存中查找变量的值，必须到主存中获取它的值，线程操作 volatile 变量都是直接操作主存
     */
    private static void test2() {
        Thread t1 = new Thread(() -> {
            while (test2Run) {

            }
        }, "t1");
        t1.start();

        // 主线程睡眠1s后，将run的值改为false。验证t1线程是否会终止运行
        Sleeper.sleep(1);
        test2Run = false;
    }

    static boolean test1Run = true;

    /**
     * 引出可见性问题:当前程序并不会因为主线程将run的值修改为false，t1线程终止运行。main 线程对 run 变量的修改对于 t 线程不可见，导致了 t 线程无法停止
     * 这背后的原理分析：
     * 1.初始状态， t 线程刚开始从主内存读取了 run 的值到工作内存。
     * 2.因为 t 线程要频繁从主内存中读取 run 的值，JIT 编译器会将 run 的值缓存至自己工作内存中的高速缓存中，减少对主存中 run 的访问，提高效率
     * 3.1 秒之后，main 线程修改了 run 的值，并同步至主存，而 t 是从自己工作内存中的高速缓存中读取这个变量的值，结果永远是旧值
     */
    private static void test1() {
        Thread t1 = new Thread(() -> {
            while (test1Run) {

            }
        }, "t1");
        t1.start();

        // 主线程睡眠1s后，将run的值改为false。验证t1线程是否会终止运行
        Sleeper.sleep(1);
        test1Run = false;
    }
}
