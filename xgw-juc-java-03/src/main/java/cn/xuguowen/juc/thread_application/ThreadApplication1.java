package cn.xuguowen.juc.thread_application;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadApplication1
 * Package: cn.xuguowen.juc.thread_application
 * Description:多线程应用之阅读华罗庚《统筹方法》，给出烧水泡茶的多线程解决方案。
 * 案例：想泡壶茶喝。当时的情况是：开水没有；水壶要洗，茶壶、茶杯要洗；火已生了，茶叶也有了。怎么办？
 * 其中用 sleep(n) 模拟洗茶壶、洗水壶等耗费的时间。
 *
 * @Author 徐国文
 * @Create 2024/5/23 19:55
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadApplication1")
public class ThreadApplication1 {

    public static void main(String[] args) {
        test1();
    }

    /**
     * 根据华罗庚的统筹方法，我们可以分析出：
     * 老王：洗水壶->烧开水（前置条件必须是水壶洗出啦）
     * 小王：在烧开水的过程中，我们可以去干这些事：洗茶壶->洗茶杯->拿茶叶->泡茶
     * 解法缺陷：
     *  1.小王等老王的水烧开了，小王泡茶，如果反过来要实现老王等小王的茶叶拿来了，老王泡茶呢？代码最好能适应两种情况。
     *  2.两个线程其实是各执行各的，如果要模拟老王把水壶交给小王泡茶，或模拟小王把茶叶交给老王泡茶呢。
     */
    private static void test1() {

        Thread t1 = new Thread(() -> {
            log.debug("洗水壶");
            Sleeper.sleep(1);
            log.debug("烧开水");
            Sleeper.sleep(5);
        }, "老王");


        Thread t2 = new Thread(() -> {
            log.debug("洗茶壶");
            Sleeper.sleep(1);
            log.debug("洗茶杯");
            Sleeper.sleep(2);
            log.debug("拿茶叶");
            Sleeper.sleep(1);

            log.debug("等待老王把水烧开了泡茶");
            try {
                t1.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("小王泡茶");
        },"小王");


        t1.start();
        t2.start();
    }
}
