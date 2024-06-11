package cn.xuguowen.juc.thread_activity;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: TestChatGPTHunngerLock
 * Package: cn.xuguowen.juc.thread_activity
 * Description:饥饿（Starvation）是一种并发问题，指的是一个线程长时间无法获得所需的资源或无法进入可运行状态，导致其无法继续执行。这通常是由于资源分配不公平或线程优先级设置不合理所导致的。
 * 1.饥饿的常见原因：
 *  - 资源分配不公平：高优先级线程总是抢占资源，低优先级线程始终得不到执行机会。
 *  - 锁竞争：某些线程长时间持有锁，导致其他需要锁的线程无法获得锁。
 *  - 不公平的调度算法：调度算法没有公平地分配CPU时间片，导致某些线程得不到执行机会。
 * 2.饥饿的案例:展示了高优先级线程不断抢占资源，导致低优先级线程长期无法执行
 * 3.解决饥饿问题的方法
 *  - Java中的ReentrantLock可以通过构造函数参数设置为公平锁，保证锁的获取顺序按照请求的顺序进行。
 *  - 避免设置过高或过低的优先级，保持线程优先级的合理分配。
 *  - 选择合理的线程调度算法，确保每个线程都有机会获得CPU时间片。
 * @Author 徐国文
 * @Create 2024/6/11 21:33
 * @Version 1.0
 */
public class TestChatGPTHunngerLock {
    public static void main(String[] args) {
        // 创建两个线程，一个设置为高优先级，一个设置为低优先级。优先级线程会频繁抢占CPU时间片，而低优先级线程可能长期无法获得执行机会。
        Thread highPriorityThread = new Thread(new Worker(), "High-Priority-Thread");
        Thread lowPriorityThread = new Thread(new Worker(), "Low-Priority-Thread");

        highPriorityThread.setPriority(Thread.MAX_PRIORITY);
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);


        highPriorityThread.start();
        lowPriorityThread.start();
    }
}


@Slf4j(topic = "c.Worker")
class Worker implements Runnable {
    /**
     * run方法模拟工作，每个线程持续工作10ms，并在每次工作后休眠10毫秒。
     */
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 10000) {
            log.debug("{} is working", Thread.currentThread().getName());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}