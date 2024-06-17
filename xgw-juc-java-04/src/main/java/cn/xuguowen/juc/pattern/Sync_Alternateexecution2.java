package cn.xuguowen.juc.pattern;

import cn.xuguowen.juc.utils.Sleeper;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: Sync_Alternateexecution2
 * Package: cn.xuguowen.juc.pattern
 * Description:同步模式之交替执行。
 * 题目要求：线程 1 输出 a 5 次，线程 2 输出 b 5 次，线程 3 输出 c 5 次。现在要求输出 abcabcabcabcabc 怎么实现。
 * 当前代码是使用ReentrantLock结合Condition实现的。
 * ReentrantLock结合Condition会存在虚假唤醒的情况。但是当前案例中不存在，因为线程的个数我们已经是明确了的。
 *
 * @Author 徐国文
 * @Create 2024/6/17 11:53
 * @Version 1.0
 */
public class Sync_Alternateexecution2 {

    public static void main(String[] args) {
        AwaitSingal awaitSingal = new AwaitSingal(5);
        Condition a = awaitSingal.newCondition();
        Condition b = awaitSingal.newCondition();
        Condition c = awaitSingal.newCondition();


        new Thread(() -> {
            awaitSingal.print("a", a, b);
        }).start();

        new Thread(() -> {
            awaitSingal.print("b", b, c);
        }).start();

        new Thread(() -> {
            awaitSingal.print("c", c, a);
        }).start();


        // 主线程睡眠1s后，发起唤醒操作
        Sleeper.sleep(1);
        awaitSingal.lock();
        try {
            System.out.println("程序的开始由主线程发起...");
            // 唤醒在a休息室等待的线程
            a.signal();
        } finally {
            awaitSingal.unlock();
        }
    }
}

class AwaitSingal extends ReentrantLock {

    // 循环打印次数
    private Integer loopNumber;

    public AwaitSingal(Integer loopNumber) {
        this.loopNumber = loopNumber;
    }

    /**
     * 打印
     *
     * @param content 打印内容
     * @param current 当前线程的休息室
     * @param next    下一个线程的休息室
     */
    public void print(String content, Condition current, Condition next) {
        for (int i = 0; i < loopNumber; i++) {
            // 获取锁资源
            this.lock();
            try {
                // 获取到锁之后，先进入自己的休息室休息。程序的开始调度是由主线程发起的
                current.await();
                // 唤醒之后要打印内容
                System.out.print(content);
                // 打印完成之后，通知下一个线程可以执行了
                next.signal();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                this.unlock();
            }
        }
    }
}
