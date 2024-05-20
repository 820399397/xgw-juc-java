package cn.xuguowen.juc.thread_method;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadMethod_Join
 * Package: cn.xuguowen.juc.thread_method
 * Description:join()方法的应用
 * 1.先给出使用join()方法的背景。（就是说为什么要使用join()方法）
 * 2.给出使用join()方法的例子
 * 3.给出使用join()方法的注意事项
 * 4.如果传递的 millis 值为 0，join(0) 的效果与 join() 一样，即等待线程结束而没有时间限制。
 *
 * @Author 徐国文
 * @Create 2024/5/20 12:37
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadMethod_Join")
public class ThreadMethod_Join {
    static int r = 0;

    public static void main(String[] args) throws InterruptedException {
        test4();
    }

    /**
     * join(int x):同步限时等待之等待时间大于线程t1运行任务的时间
     */
    private static void test4() throws InterruptedException {
        log.debug("开始");
        Thread t1 = new Thread(() -> {
            log.debug("t1 thread running...");
            Sleeper.sleep(1);
            r = 10;
        }, "t1");
        t1.start();

        // 主线程等待t1线程2s，如果2s之内t1线程就运行完毕了，那主线程也不用一直等待了，直接结束等待。
        t1.join(2000);
        log.debug("main thread running... r：{}",r);
        log.debug("结束");

    }

    /**
     * join(int x):同步限时等待之等待时间小于线程t1运行任务的时间
     */
    private static void test3() throws InterruptedException {
        log.debug("开始");
        Thread t1 = new Thread(() -> {
            log.debug("t1 thread running...");
            Sleeper.sleep(1);
            r = 10;
        }, "t1");
        t1.start();

        // 主线程等待t1线程0.5s，如果0.5s之后t1线程还没有运行结束，那主线程就不等待了
        t1.join(500);
        log.debug("main thread running... r：{}",r);
        log.debug("结束");

    }

    /**
     * join()方法：同步一直等待
     * @throws InterruptedException
     */
    private static void test2() throws InterruptedException {
        log.debug("开始");
        Thread t1 = new Thread(() -> {
            log.debug("t1 thread running...");
            Sleeper.sleep(1);
            r = 10;
        }, "t1");
        t1.start();

        // 主线程等待t1线程运行完毕之后，再继续执行
        t1.join();
        log.debug("main thread running... r：{}",r);
        log.debug("结束");
    }

    /**
     * 1.需求：如下代码运行之后，r的结果是 0 。我现在就想要得到r的结果是 10，怎么做？
     * 2.如下代码执行后的结果是 r = 0; 分析：
     *  因为主线程和线程 t1 是并行执行的，t1 线程需要 1 秒之后才能算出 r=10。而主线程一开始就要打印 r 的结果，所以只能打印出 r=0
     * 3.解决方法：
     *      用 sleep 行不行？（不太靠谱，因为我并不清楚t1线程多长时间内可以执行完自己的任务。）
     *      用 join，加在 t1.start() 之后即可
     */
    private static void test1() {
        log.debug("开始");
        Thread t1 = new Thread(() -> {
            log.debug("t1 thread running...");
            Sleeper.sleep(1);
            r = 10;
        }, "t1");
        t1.start();

        log.debug("main thread running... r：{}",r);
        log.debug("结束");
    }
}
