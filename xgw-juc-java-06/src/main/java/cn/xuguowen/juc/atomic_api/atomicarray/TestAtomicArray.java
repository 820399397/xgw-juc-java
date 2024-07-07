package cn.xuguowen.juc.atomic_api.atomicarray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * ClassName: TestAtomicArray
 * Package: cn.xuguowen.juc.atomic_api.atomicarray
 * Description:原子数组：是Java并发编程中的一种数据结构，提供了一种可以在多个线程之间安全共享和更新的方式。Java中的原子数组主要通过java.util.concurrent.atomic包中的类来实现，其中包括以下几种原子数组类型：
 * - AtomicIntegerArray：用于处理int类型数组的原子操作。
 * - AtomicLongArray：用于处理long类型数组的原子操作。-
 * - AtomicReferenceArray<E>：用于处理对象引用数组的原子操作。
 * 这些原子数组类都提供了一组原子操作方法，可以确保在多线程环境下对数组元素的操作是线程安全的。
 * 这些方法通过底层的硬件支持确保了原子性，避免了传统的同步机制，从而提高了性能。
 *
 * @Author 徐国文
 * @Create 2024/7/7 14:56
 * @Version 1.0
 */
public class TestAtomicArray {

    public static void main(String[] args) {
        // new int[10]数组是线程不安全的实现：[3974, 3929, 3946, 3944, 3959, 3999, 3955, 3943, 3939, 3956]
        demo(
                () -> new int[10],
                (array) -> array.length,
                (array, index) -> array[index]++,
                (array) -> System.out.println(Arrays.toString(array))
        );

        // new AtomicIntegerArray(10)是线程安全的实现：[10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000]
        // 注意：是对数组元素的操作是线程安全的。
        demo(
                () -> new AtomicIntegerArray(10),
                (array) -> array.length(),
                (array, index) -> array.getAndIncrement(index),
                (array) -> System.out.println(array)
        );
    }

    /**
     * 参数1，提供数组、可以是线程不安全数组或线程安全数组
     * 参数2，获取数组长度的方法
     * 参数3，自增方法，回传 array, index
     * 参数4，打印数组的方法
     */
    // supplier 提供者 无中生有 ()->结果
    // function 函数 一个参数一个结果 (参数)->结果 , BiFunction (参数1,参数2)->结果
    // consumer 消费者 一个参数没结果 (参数)->void, BiConsumer (参数1,参数2)->
    private static <T> void demo(
            Supplier<T> arraySupplier,
            Function<T, Integer> lengthFun,
            BiConsumer<T, Integer> putConsumer,
            Consumer<T> printConsumer) {
        // 创建一个线程列表 ts 来存储线程对象。
        List<Thread> ts = new ArrayList<>();
        // 使用 arraySupplier 获取数组实例 array
        T array = arraySupplier.get();
        // 使用 lengthFun 获取数组长度 length。
        int length = lengthFun.apply(array);
        // 这一段代码创建了 length 个线程，并将每个线程添加到 ts 列表中。每个线程都执行以下操作：
        for (int i = 0; i < length; i++) {
            // 创建一个新线程，线程的任务是对数组进行 10000 次操作。
            ts.add(new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    // 在每次操作中，使用 putConsumer 对数组的某个位置进行操作。具体操作由 putConsumer 实现。操作的索引是 j % length，这样可以确保索引在数组范围内循环。
                    putConsumer.accept(array, j % length);
                }
            }));
        }
        // 启动所有线程：
        ts.forEach(t -> t.start());
        // 等待所有线程结
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 打印数组内容
        printConsumer.accept(array);
    }
}
