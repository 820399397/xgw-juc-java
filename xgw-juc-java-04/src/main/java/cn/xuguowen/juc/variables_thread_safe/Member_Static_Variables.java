package cn.xuguowen.juc.variables_thread_safe;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Member_Static_Variables
 * Package: cn.xuguowen.juc.thread_safe
 * Description:演示成员变量和静态变量的线程安全问题
 *
 * @Author 徐国文
 * @Create 2024/5/26 15:45
 * @Version 1.0
 */
@Slf4j(topic = "c.Member_Static_Variables")
public class Member_Static_Variables {


    private static int counter = 0;

    /**
     * 由于 counter 是一个静态变量，两个线程 t1 和 t2 都会访问并修改它。由于 counter++ 和 counter-- 操作不是原子操作，它们可能会被多个线程同时执行，导致数据竞争和不可预期的结果。
     * 可以从counter++ / uconter-- 的字节码角度理解。
     * 0: getstatic     #2                  // Field counter:I
     * 3: iconst_1
     * 4: iadd
     * 5: putstatic     #2                  // Field counter:I
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                counter++;
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                counter--;
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        log.debug("counter:{}", counter);
    }

    /**
     * 多个线程共享同一个对象实例并对其成员变量有读写操作，则这些成员变量是非线程安全的，需要同步机制来保护。
     */
    private static void test2() {
        Example example = new Example();
        new Thread(() -> {
            example.increment();
        }).start();

        new Thread(() -> {
            example.increment();
        }).start();
        System.out.println(example.getCounter());
    }

    /**
     * 每个线程都有自己的 Example 实例，互不干扰，因此成员变量 counter 是线程安全的。
     */
    private static void test1() {
        new Thread(() -> {
            Example example = new Example();
            example.increment();
            System.out.println(example.getCounter());
        }).start();

        new Thread(() -> {
            Example example = new Example();
            example.increment();
            System.out.println(example.getCounter());
        }).start();
    }
}


class Example {
    private int counter = 0;

    public void increment() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }
}