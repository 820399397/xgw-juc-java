package cn.xuguowen.juc.thread_safe_example_analysis;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName: Test1
 * Package: cn.xuguowen.juc.thread_safe_example_analysis
 * Description:分析如下代码是否是线程安全的。
 *
 * @Author 徐国文
 * @Create 2024/5/27 13:10
 * @Version 1.0
 */
@Slf4j(topic = "c.Test1")
public class Test1 {
    private static Integer i = 0;

    // 使用 AtomicInteger 可以确保线程安全，因为 AtomicInteger 提供了原子的自增操作。
    // private static AtomicInteger i = new AtomicInteger(0);

    public static void main(String[] args) {
        // 创建线程列表
        List<Thread> list = new ArrayList<>();

        for (int j = 0; j < 2; j++) {
            // 创建了两个线程，每个线程执行 5000 次自增操作。在每次自增操作时，通过 synchronized 关键字对 i 进行同步，以确保同一时间只有一个线程能够修改 i。
            // 线程不安全的：
            // 分析如下：
            // 尽管你在每次自增操作时使用了 synchronized 对 i 进行同步，但由于 Integer 是不可变对象，这种同步机制并不能保证 i 的值是线程安全的。
            // 尽管在每次自增操作时使用了 synchronized 对 i 进行了同步，但这里有一个潜在的问题：i 是一个 Integer 对象，而 Integer 是不可变的。
            // 在 synchronized 块中，i++ 实际上是先读取 i 的值，增加 1，然后将新值赋给 i。因为 Integer 是不可变的，每次赋值都会生成一个新的 Integer 对象。因此，synchronized (i) 锁定的其实是不同的 Integer 对象，而不是同一个对象。
            // synchronized (i) 锁定的是当前的 i 对象，但 i++ 会创建一个新的 Integer 对象，因此下一次循环时 synchronized 锁定的是一个新的对象，而不是原来的那个对象。
            Thread thread = new Thread(() -> {
                for (int k = 0; k < 5000; k++) {
                    synchronized (i) {
                        i++;
                    }
                }
            }, "" + j);
            list.add(thread);
        }

        // 启动线程
        list.stream().forEach(t -> t.start());

        // 主线程等待所有线程执行完毕
        list.stream().forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 打印结果
        log.debug("{}", i);
    }
}
