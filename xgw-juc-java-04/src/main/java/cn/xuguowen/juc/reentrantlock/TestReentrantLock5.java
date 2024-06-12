package cn.xuguowen.juc.reentrantlock;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: TestReentrantLock5
 * Package: cn.xuguowen.juc.reentrantlock
 * Description:测试ReentrantLock的条件变量：条件变量允许线程在特定的条件下等待（类似于 Object 的 wait 和 notify 机制），并在条件满足时被通知唤醒。
 * ReentrantLock 中的条件变量通过 newCondition() 方法来创建。每个条件变量都与一个锁相关联，并且必须在获取了锁之后才能使用。
 *
 * 1.区别：synchronized 中也有条件变量，就是那个 waitSet 休息室，当条件不满足时进入 waitSet 等待。
 *        ReentrantLock 的条件变量比 synchronized 强大之处在于，它是支持多个条件变量的，这就好比
 *          - synchronized 是那些不满足条件的线程都在一间休息室等消息
 *          - 而 ReentrantLock 支持多间休息室，有专门等烟的休息室、专门等早餐的休息室、唤醒时也是按休息室来唤醒
 * 2.使用要点：
 *  - await 前需要获得锁
 *  - await 执行后，会释放锁，进入 conditionObject 等待
 *  - await 的线程被唤醒（或打断、或超时）后重新竞争 lock 锁
 *  - 竞争 lock 锁成功后，从 await 后继续执行
 *
 * 3.使用场景：条件变量的典型使用场景是生产者-消费者问题、资源池管理等需要复杂线程协调的场景。
 * @Author 徐国文
 * @Create 2024/6/12 22:02
 * @Version 1.0
 */
@Slf4j(topic = "c.TestReentrantLock5")
public class TestReentrantLock5 {


    // 条件变量：分别表示送烟和送外卖的条件
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    // ReentrantLock 对象本身就是一个锁
    // 首先创建一个 ReentrantLock 和两个与之相关联的 Condition
    static final ReentrantLock lock = new ReentrantLock();

    // 吸烟室
    static Condition waitCigaretteQueue = lock.newCondition();

    // 吃饭的地方
    static Condition waitTakeoutQueue = lock.newCondition();

    public static void main(String[] args) {
        test1();

    }


    /**
     * 解决方法：用 while + wait，当条件不成立，再次 wait
     */
    private static void test1() {
        // 小南线程干活的前提条件是有烟.没烟就去等待了
        new Thread(() -> {
            lock.lock();

            try {
                log.debug("有烟没？[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        // 线程在获取锁后检查条件是否满足。如果条件不满足，则调用 condition.await() 进入等待状态，释放锁，并等待条件被通知。
                        // 当前线程等待，并释放锁，直到被通知或被中断。
                        waitCigaretteQueue.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("有烟了，可以开始干活了");
            } finally {
                lock.unlock();
            }
        }, "小南").start();

        // 小南线程干活的前提条件是有外卖送到了.没外卖就去等待了
        new Thread(() -> {
            lock.lock();
            try {
                log.debug("外卖送到没？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        waitTakeoutQueue.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                log.debug("可以开始干活了");
            } finally {
                lock.unlock();
            }
        }, "小女").start();

        // 主线程睡眠1s后，再启动一个送外卖线程去送外卖
        Sleeper.sleep(1);
        new Thread(() -> {
            lock.lock();
            try {
                hasTakeout = true;
                log.debug("外卖到了奥！");
                // 线程在获取锁后设置条件为满足状态，并调用 condition.signal() 通知等待的线程条件已经满足。
                // 通知一个等待的线程（如果有）条件已经满足。
                waitTakeoutQueue.signal();
            } finally {
                lock.unlock();
            }
        }, "送外卖的").start();

        new Thread(() -> {
            lock.lock();
            try {
                hasCigarette = true;
                log.debug("香烟到了奥！");
                waitCigaretteQueue.signal();
            } finally {
                lock.unlock();
            }
        }, "送香烟的").start();
    }
}
