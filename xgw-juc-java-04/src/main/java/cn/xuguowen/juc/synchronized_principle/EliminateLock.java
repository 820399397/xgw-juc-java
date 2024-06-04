package cn.xuguowen.juc.synchronized_principle;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.security.acl.Owner;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: EliminateLock
 * Package: cn.xuguowen.juc.synchronized_principle
 * Description:锁消除：是一种优化技术，用于在编译器级别消除不必要的同步操作，从而提高程序的性能。
 * 在Java中，同步块（synchronized block）或者同步方法（synchronized method）通常用于确保多线程环境下共享资源的安全访问。
 * 然而，在某些情况下，编译器可以通过静态分析发现，某个同步块内部的锁定操作实际上是多余的。
 * 例如，在某些情况下，同步块中的共享资源只在单线程中被访问，或者共享资源的访问被封装在其他方式的同步控制之下（比如使用ThreadLocal变量），这时就可以将同步块内部的锁定操作消除掉，从而提高程序的执行效率。
 * 编译器可以在编译过程中进行锁消除优化。当编译器发现某个同步块内部的锁定操作可以被消除时，它会直接将同步块内的代码替换为非同步的形式，从而避免了锁的竞争和额外的开销。
 * 需要注意的是，锁消除优化是一种编译器级别的优化，它需要依赖于编译器的静态分析能力。如果编译器无法确定某个同步块内部的锁定操作是否可以被消除，那么就不会进行锁消除优化。
 * <p>
 * 使用JMT评估如下代码的执行效率。
 * 案例场景：
 * - 存在锁消除
 * - 通过JVM参数关闭锁消除  -XX:-EliminateLocks
 *
 * @Author 徐国文
 * @Create 2024/6/4 20:26
 * @Version 1.0
 */

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class EliminateLock {

    static int x = 0;

    // 锁消除情况下：
    // EliminateLock.test1  avgt    5  0.344 ± 0.012  ns/op
    // EliminateLock.test2  avgt    5  0.338 ± 0.013  ns/op

    // 关闭锁消除情况下：效率就非常明显了
    // EliminateLock.test1  avgt    5   0.336 ± 0.007  ns/op
    // EliminateLock.test2  avgt    5  13.715 ± 0.594  ns/op
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(EliminateLock.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3)
    @Measurement(iterations = 5)
    @Fork(1)
    public static void test1() {
        x++;
    }


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3)
    @Measurement(iterations = 5)
    @Fork(1)
    public static void test2() {
        Object o = new Object();
        synchronized (o) {
            x++;
        }
    }

}
