package cn.xuguowen.juc.sharing_issues;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Test
 * Package: cn.xuguowen.juc.sharing_issues
 * Description:演示Java中共享问题的体现
 * 1.你或许理解的是：一个线程对count++的操作，进行了5000次。另一个线程对count--的操作，进行了5000次。最终的结果为 0 。
 *   其实不然。正是因为分时系统下产生了线程上下文切换的场景，所以导致jvm指令交错执行，最终结果也不是0的问题。
 * 2.这其实就是线程安全问题。
 * 3.临界区 Critical Section：一段代码块内如果存在对共享资源的多线程读写操作，称这段代码块为临界区
 *      - 一个程序运行多个线程本身是没有问题的。问题出在多个线程访问共享资源。
 *      - 其中，多个线程**读**共享资源其实也没有问题；在多个线程对共享资源**读写**操作时发生指令交错，就会出现问题。
 * 4.竞态条件 Race Condition：多个线程在临界区内执行，由于代码的执行序列不同而导致结果无法预测，称之为发生了竞态条件
 *
 * @Author 徐国文
 * @Create 2024/5/24 12:18
 * @Version 1.0
 */
@Slf4j(topic = "c.Test1")
public class Test1 {

    static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                // 临界区
                count++;
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                // 临界区
                count--;
            }
        }, "t2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        log.debug("count:{}", count);
    }
}
