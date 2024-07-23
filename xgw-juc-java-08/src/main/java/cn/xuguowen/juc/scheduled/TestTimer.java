package cn.xuguowen.juc.scheduled;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

/**
 * ClassName: TestTimer
 * Package: cn.xuguowen.juc.scheduled
 * Description:在『任务调度线程池』功能加入之前，可以使用 java.util.Timer 来实现定时功能，Timer 的优点在于简单易用.
 * 但由于所有任务都是由同一个线程来调度，因此所有任务都是串行执行的，同一时间只能有一个任务在执行，前一个任务的延迟或异常都将会影响到之后的任务。
 *
 * @Author 徐国文
 * @Create 2024/7/23 12:28
 * @Version 1.0
 */
@Slf4j(topic = "c.TestTimer")
public class TestTimer {

    public static void main(String[] args) {
        // 测试timer
        // testNormal();

        // 测试timer单线程导致的问题1
        // testException1();

        // 测试timer单线程导致的问题2
        testException2();

    }

    /**
     * timer中的t1任务执行发生异常了，导致t2任务并没有执行.
     * 16:13:29 [Timer-0] c.TestTimer - task 1
     * Exception in thread "Timer-0" java.lang.ArithmeticException: / by zero
     * 	at cn.xuguowen.juc.scheduled.TestTimer$1.run(TestTimer.java:41)
     * 	at java.util.TimerThread.mainLoop(Timer.java:555)
     * 	at java.util.TimerThread.run(Timer.java:505)
     */
    private static void testException2() {
        Timer timer = new Timer();
        TimerTask t1 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 1");
                int i = 1 / 0;
            }
        };

        TimerTask t2 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 2");
            }
        };

        // 使用timer添加2个任务，希望他们都在1秒后执行。
        // 但由于 timer 内只有一个线程来顺序执行队列中的任务，因此『任务1』发生了异常，导致『任务2』 无法执行了
        timer.schedule(t1,1000);
        timer.schedule(t2,1000);
    }

    /**
     * timer中的t1任务执行延时了，导致t2任务并没有在1s后立刻执行.而是等t1任务执行完毕之后才去执行的t2任务
     * 16:10:08 [Timer-0] c.TestTimer - task 1
     * 16:10:10 [Timer-0] c.TestTimer - task 2
     */
    private static void testException1() {
        Timer timer = new Timer();
        TimerTask t1 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 1");
                Sleeper.sleep(2);
            }
        };

        TimerTask t2 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 2");
            }
        };

        // 使用timer添加2个任务，希望他们都在1秒后执行。
        // 但由于 timer 内只有一个线程来顺序执行队列中的任务，因此『任务1』的延时，影响了『任务2』的执行
        timer.schedule(t1,1000);
        timer.schedule(t2,1000);

    }

    private static void testNormal() {
        Timer timer = new Timer();
        TimerTask t1 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 1");
            }
        };

        TimerTask t2 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 2");
            }
        };

        // 使用timer添加2个任务，希望他们都在1秒后执行。但是timer的本质是单线程的，只有一个线程去执行的
        timer.schedule(t1,1000);
        timer.schedule(t2,1000);
    }

}
