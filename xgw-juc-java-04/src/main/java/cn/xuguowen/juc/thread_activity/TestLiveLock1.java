package cn.xuguowen.juc.thread_activity;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: LiveLock
 * Package: cn.xuguowen.juc.thread_activity
 * Description:活锁：是一种并发问题，类似于死锁，但有所不同。在活锁中，线程并没有被阻塞，而是不断地尝试解决冲突，但由于每次尝试都失败，导致线程一直在循环中忙碌，无法完成实际工作。
 * 1.特点：
 *  - 与死锁不同，线程在活锁情况下不是一直等待，而是不断地在尝试某种操作。
 *  - 尽管线程在运行，但由于反复尝试和失败，整个系统的进展实际上被阻塞了。
 * 2.案例场景：活锁出现在两个线程互相改变对方的结束条件，最后谁也无法结束。
 * @Author 徐国文
 * @Create 2024/6/11 20:55
 * @Version 1.0
 */
@Slf4j(topic = "c.TestLiveLock1")
public class TestLiveLock1 {
    private static int count = 10;


    public static void main(String[] args) {
        new Thread(() -> {
            // 期望减到 0 退出循环
            while (count > 0) {
                Sleeper.sleep(0.2);
                count--;
                log.debug("count: {}", count);
            }
        }, "t1").start();

        new Thread(() -> {
            // 期望减到 0 退出循环
            while (count < 20) {
                Sleeper.sleep(0.2);
                count++;
                log.debug("count: {}", count);
            }
        }, "t2").start();
    }
}


