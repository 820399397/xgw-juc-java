package cn.xuguowen.juc.park_unpark;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 * ClassName: Thread_Park_Unparl
 * Package: cn.xuguowen.juc.park_unpark
 * Description:park()和unpark()方法的演示以及原理。
 * 1.Park（挂起）：当一个线程被挂起时，它会暂时停止执行，并且不会被调度到处理器上运行。这可以是由于某些条件暂时不满足，或者由于操作系统决定将其挂起以便更高优先级的任务能够获得执行，或者出于其他调度策略的考虑。
 * 2.Unpark（解除挂起）：当一个线程被解除挂起时，它之前因为某种原因被挂起的状态会被解除，允许它重新参与到调度中，并有机会被调度到处理器上执行。
 * 3.特点：与 Object 的 wait & notify 相比
 *  - wait，notify 和 notifyAll 必须配合 Object Monitor 一起使用，而 park，unpark 不必
 *  - park & unpark 是以线程为单位来【阻塞】和【唤醒】线程，而 notify 只能随机唤醒一个等待线程，notifyAll 是唤醒所有等待线程，就不那么【精确】
 *  - park & unpark 可以先 unpark，而 wait & notify 不能先 notify
 * 4.原理：每个线程都有自己的一个 Parker 对象，由三部分组成 _counter ， _cond 和 _mutex
 * 打个比喻：线程就像一个旅人，Parker 就像他随身携带的背包，条件变量就好比背包中的帐篷。_counter 就好比背包中的备用干粮（0 为耗尽，1 为充足）
 * 调用 park 就是要看需不需要停下来歇息
 *  - 如果备用干粮耗尽，那么钻进帐篷歇息
 *  - 如果备用干粮充足，那么不需停留，继续前进
 * 调用 unpark，就好比令干粮充足
 *  - 如果这时线程还在帐篷，就唤醒让他继续前进
 *  - 如果这时线程还在运行，那么下次他调用 park 时，仅是消耗掉备用干粮，不需要停留继续前进。注意：因为背包空间有限，多次调用 unpark 仅会补充一份备用干粮
 *      正是这一点，解释了test2()方法的运行。
 *
 *
 * @Author 徐国文
 * @Create 2024/6/7 12:23
 * @Version 1.0
 */
@Slf4j(topic = "c.Thread_Park_Unpark")
public class Thread_Park_Unpark {

    public static void main(String[] args) {
        // test1();

        test2();
    }

    /**
     * t1线程睡眠2秒，然后主线程在1s之后就释放了t1线程的阻塞（此时t1线程还没有调用paro方法阻塞呢），那2s之后，t1线程会进入park阻塞状态码?
     * 答案是不会。这和paro unpark的原理有关。
     * 先看运行结果
     */
    public static void test2() {
        Thread t1 = new Thread(() -> {
            log.debug("start...");
            Sleeper.sleep(2);

            // 暂停t1线程
            log.debug("park...");
            LockSupport.park();
            log.debug("resume...");
        }, "t1");

        t1.start();

        Sleeper.sleep(1);
        log.debug("unpark...");
        LockSupport.unpark(t1);
    }

    /**
     * t1线程睡眠1s之后，调用park方法阻塞了。
     * 与此同时，主线程睡眠2s之后，调用unpark方法，释放了t1线程的阻塞。
     * 这是一个正常的情况
     */
    public static void test1() {
        Thread t1 = new Thread(() -> {
            log.debug("start...");
            Sleeper.sleep(1);

            // 暂停t1线程
            log.debug("park...");
            LockSupport.park();
            log.debug("resume...");
        }, "t1");

        t1.start();

        Sleeper.sleep(2);
        log.debug("unpark...");
        LockSupport.unpark(t1);
    }
}
