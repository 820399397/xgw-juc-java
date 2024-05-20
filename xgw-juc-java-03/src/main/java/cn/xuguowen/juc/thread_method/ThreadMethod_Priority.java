package cn.xuguowen.juc.thread_method;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadMethod_Priority
 * Package: cn.xuguowen.juc.thread_method
 * Description:线程优先级方法 setPriority() / getPriority()
 * 1.线程优先级会提示（hint）调度器优先调度该线程，但它仅仅是一个提示，调度器可以忽略它。有点类似于yield()方法，存在想让出时间片但没有让出去的场景。
 * 2.如果 cpu 比较忙，那么优先级高的线程会获得更多的时间片，但 cpu 闲时，优先级几乎没作用
 * 3.本次基于如下代码测试3种情况：
 * ① 不存在yield()方法和设置线程优先级的方法，执行如下代码，期望得到的结果是两个线程打印的count大小是差不多的
 * ② 线程2内部的任务本身调用了yield()方法，执行如下代码，期望得到的结果是两个线程打印的count大小，线程2的count会小于线程1的count很多
 * ③ 线程1的优先级设置为最低，线程2的优先级设置为最大，执行如下代码，期望得到的结果是线程2的count会大于线程1的count很多
 * 结论：我电脑执行发现这3种情况的结果下，两个线程内部的count都差不多，再一次说明了优先级和yield()只是一个提示，并不是绝对的。具体还得看操作系统的任务调度器
 *
 * @Author 徐国文
 * @Create 2024/5/20 11:54
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadMethod_Priority")
public class ThreadMethod_Priority {
    public static void main(String[] args) {
        Runnable task1 = () -> {
            int count = 0;
            for (; ; ) {
                System.out.println("----->1 " + count++);
            }
        };

        Runnable task2 = () -> {
            int count = 0;
            for (; ; ) {
                // Thread.yield();
                System.out.println("        ----->2 " + count++);
            }
        };

        Thread t1 = new Thread(task1, "t1");
        Thread t2 = new Thread(task2, "t2");

        t1.setPriority(Thread.MIN_PRIORITY);
        t2.setPriority(Thread.MAX_PRIORITY);

        t1.start();
        t2.start();
    }

}
