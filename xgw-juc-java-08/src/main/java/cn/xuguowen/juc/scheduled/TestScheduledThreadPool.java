package cn.xuguowen.juc.scheduled;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: TestScheduledThreadPool
 * Package: cn.xuguowen.juc.scheduled
 * Description:测试定时任务的线程池
 * 在Java中，ScheduledExecutorService 接口提供了两种常用的方法来调度任务：scheduleWithFixedDelay 和 scheduleAtFixedRate。虽然它们都用于定时调度任务，但它们的行为有所不同。下面是它们的区别和用法：
 * - scheduleWithFixedDelay 方法在前一个任务完成之后等待一段固定的时间，然后再开始下一个任务。如果任务执行时间较长，会导致下一次任务的开始时间延后。
 * - scheduleAtFixedRate 方法在每个任务开始之间保持一个固定的时间间隔，无论前一个任务是否完成。如果任务执行时间较长，可能会导致任务重叠执行。
 *
 * @Author 徐国文
 * @Create 2024/7/23 16:15
 * @Version 1.0
 */
@Slf4j(topic = "c.TestScheduledThreadPool")
public class TestScheduledThreadPool {

    public static void main(String[] args) {
        // test1();

        // test2();

        // test_scheduleAtFixedRate();

        // test_scheduleAtFixedRate_exceed();

        test_scheduleWithFixedDelay();


    }

    /**
     * 测试scheduleWithFixedDelay例子：：一开始，延时 1s，scheduleWithFixedDelay 的间隔是 上一个任务结束 <-> 延时 <-> 下一个任务开始 所以间隔都是 3s
     * 16:34:33 [main] c.TestScheduledThreadPool - start...
     * 16:34:34 [pool-1-thread-1] c.TestScheduledThreadPool - running...
     * 16:34:37 [pool-1-thread-1] c.TestScheduledThreadPool - running...
     * 16:34:40 [pool-1-thread-1] c.TestScheduledThreadPool - running...
     * 16:34:43 [pool-1-thread-1] c.TestScheduledThreadPool - running...
     */
    private static void test_scheduleWithFixedDelay() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        log.debug("start...");

        pool.scheduleWithFixedDelay(()-> {
            log.debug("running...");
            Sleeper.sleep(2);
        }, 1, 1, TimeUnit.SECONDS);

    }

    /**
     * 测试scheduleAtFixedRate 例子（任务执行时间超过了间隔时间）：一开始，延时 1s，接下来，由于任务执行时间 > 间隔时间，间隔被『撑』到了 2s
     * 16:33:24 [main] c.TestScheduledThreadPool - start...
     * 16:33:25 [pool-1-thread-1] c.TestScheduledThreadPool - running...
     * 16:33:27 [pool-1-thread-1] c.TestScheduledThreadPool - running...
     * 16:33:29 [pool-1-thread-1] c.TestScheduledThreadPool - running...
     * 16:33:31 [pool-1-thread-1] c.TestScheduledThreadPool - running...
     */
    private static void test_scheduleAtFixedRate_exceed() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        log.debug("start...");

        // 任务定时执行
        pool.scheduleAtFixedRate(() -> {
            log.debug("running...");
            Sleeper.sleep(2);
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 测试任务的定时执行
     */
    private static void test_scheduleAtFixedRate() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        log.debug("start...");

        // 任务定时执行
        pool.scheduleAtFixedRate(() -> {
            log.debug("running...");
        }, 1, 1, TimeUnit.SECONDS);
    }

    private static void test2() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        // 因为有2个线程的，即使线程1出现了延时或者异常，都不会影响线程2的执行的
        executorService.schedule(() -> {
            Sleeper.sleep(2);
            log.debug("Task 1");
        }, 1, TimeUnit.SECONDS);

        executorService.schedule(() -> {
            log.debug("Task 2");
        }, 1, TimeUnit.SECONDS);
    }

    /**
     * 测试定时任务的线程池
     */
    private static void test1() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        // 添加两个任务，希望它们都在 1s 后执行
        executorService.schedule(() -> log.debug("Task 1"), 1, TimeUnit.SECONDS);
        executorService.schedule(() -> log.debug("Task 2"), 1, TimeUnit.SECONDS);
    }
}
