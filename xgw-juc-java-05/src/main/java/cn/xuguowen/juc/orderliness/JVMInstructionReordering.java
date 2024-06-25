package cn.xuguowen.juc.orderliness;

/**
 * ClassName: JVMInstructionReordering
 * Package: cn.xuguowen.juc.orderliness
 * Description:介绍了CPU的指令重排序了，现在看下JVM的指令的重排序。
 * 1.JVM 会在不影响正确性的前提下，可以调整语句的执行顺序。
 * 2.为什么要进行指令重排的优化呢？可以从CPU的指令重排的角度去理解。就是为了提高指令的吞吐率。
 * 3.什么是指令重排：是指编译器或处理器为了优化程序性能，对代码中的指令顺序进行调整，以提高执行效率。指令重排的目的是利用处理器的流水线能力和多核处理能力，使得处理器的资源得到更充分的利用，从而提升程序的执行速度。
 * 4.编译器重排：编译器在编译期间对源代码进行优化时，可能会重新排列指令，以消除不必要的等待时间，减少内存访问次数或提高缓存命中率。
 *              例如，编译器可能会将没有数据依赖关系的指令调整顺序，以便更好地利用处理器的流水线。
 * 5.处理器重排：现代处理器通过乱序执行（out-of-order execution）技术，在硬件层面对指令进行重新排序。处理器会分析指令之间的依赖关系，尽量在等待某些指令的结果时，先执行其他独立的指令。
 *              例如，处理器在执行指令时，可能会提前执行一些不依赖于当前数据的指令，以充分利用处理器的执行单元。
 * 6.指令重排的影响：虽然指令重排可以显著提高程序的执行效率，但它也可能引入一些问题，特别是在多线程编程中：
 *  - 内存可见性问题：在多线程环境中，指令重排可能导致一个线程看到的变量值与另一个线程看到的不一致，从而引发内存可见性问题。
 *                  例如，一个线程可能在变量初始化之前读取到其值，因为指令重排导致初始化和读取顺序被打乱。
 *  - 竞态条件：多线程程序中的竞态条件可能因为指令重排而更难预测和重现，从而导致程序错误。
 *
 * 7.为了防止指令重排带来的问题，可以使用以下方法：
 *  - 内存屏障（Memory Barriers）：内存屏障是一种硬件指令，用于阻止处理器对屏障两侧的指令进行重排，从而确保特定的内存操作顺序。
 *  - volatile关键字：在Java中，使用 volatile 关键字可以确保变量的读写操作不会被重排，并且对一个 volatile 变量的写操作对所有线程可见。但是， volatile 仅能保证对单个变量的操作有序，对多个变量的操作顺序不保证。
 *  - 同步机制：使用锁（如 synchronized 块或 ReentrantLock）可以确保在临界区内的指令不会被重排，从而保证线程安全。
 *
 * @Author 徐国文
 * @Create 2024/6/25 11:38
 * @Version 1.0
 */
public class JVMInstructionReordering {

    static int i;
    static int j;

    public static void main(String[] args) {
        test1();
    }

    /**
     * 通过如下案例引出JVM中的指令重排。
     * 需要注意的是：虽然当前例子在经过指令重排后的优化，不会影响最终的结果。
     */
    public static void test1() {
        Thread thread = new Thread(() -> {
            // 可以看到，至于是先执行 i 还是 先执行 j ，对最终的结果不会产生影响。所以，下面代码真正执行时，既可以是
            // i = 1;
            // j = 2;

            j = 2;
            i = 1;
        }, "t1");

        thread.start();
    }


}