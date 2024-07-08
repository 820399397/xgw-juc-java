package cn.xuguowen.juc.atomic_api.accumulator;

import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * ClassName: TestLongAdder
 * Package: cn.xuguowen.juc.atomic_api.accumulator
 * Description:测试原子累加器：是一种用于在并发环境中安全地进行计数或累加操作的工具。它们通常用于统计计数、累加值等场景，提供比传统锁机制更高效的操作方式。
 * AtomicLong:是 Java 提供的一个类，用于实现线程安全的 long 类型变量的操作。它的主要特点包括：
 * - 原子性操作：提供了一组方法（如 incrementAndGet、decrementAndGet、addAndGet 等）确保操作是原子的，避免了线程安全问题。
 * - CAS 操作：使用硬件支持的 Compare-And-Swap (CAS) 指令来实现原子性操作，而不是使用传统的锁机制。
 * LongAdder:是 Java 8 引入的新类，与 AtomicLong 相比，它在高并发情况下提供了更高的性能。它的主要特点包括：
 * - 分段累加：LongAdder 通过内部维护一个分段数组，每个线程在累加时只会更新其对应的分段，减少了竞争。
 * - 最终合并：在需要获取总和时，将所有分段的值进行合并。
 * 虽然 AtomicLong 已经提供了线程安全的累加操作，但在高并发环境下，多个线程同时更新同一个 AtomicLong 可能会导致性能下降，因为所有线程都在争夺同一个原子变量的更新权。
 * 而 LongAdder 的分段累加机制可以有效降低这种竞争，提供更高的吞吐量。
 *
 * @Author 徐国文
 * @Create 2024/7/8 12:19
 * @Version 1.0
 */
public class TestLongAdder {

    public static void main(String[] args) {
        // 原子累加器    20000000 cost:64
        demo(() -> new LongAdder(), adder -> adder.increment());

        // atomicLong   20000000 cost:366
        demo(() -> new AtomicLong(), adder -> adder.getAndIncrement());
    }

    private static <T> void demo(Supplier<T> adderSupplier, Consumer<T> action) {
        T adder = adderSupplier.get();
        long start = System.nanoTime();
        List<Thread> ts = new ArrayList<>();
        // 4 个线程，每人累加 50 万
        for (int i = 0; i < 40; i++) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    action.accept(adder);
                }
            }));
        }
        ts.forEach(t -> t.start());
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(adder + " cost:" + (end - start) / 1000_000);
    }
}
