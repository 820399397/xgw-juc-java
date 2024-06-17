package cn.xuguowen.juc.pattern;

import javafx.concurrent.Worker;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: Sync_Alternateexecution4
 * Package: cn.xuguowen.juc.pattern
 * Description:同步模式之交替执行。
 * 题目要求：三个线程分别顺序打印0-100，怎么实现。
 * Thread-0:0
 * Thread-1:1
 * Thread-2:2
 * Thread-0:3
 * Thread-1:4
 * Thread-2:5
 * ...
 *
 * @Author 徐国文
 * @Create 2024/6/17 15:36
 * @Version 1.0
 */
@Slf4j(topic = "c.Sync_Alternateexecution4")
public class Sync_Alternateexecution4 {

    // 计数器
    private static int count = 0;

    // 启动线程数
    private static int loopNumber = 3;

    // 锁对象
    private static final Object LOCK = new Object();

    // 打印的最大数
    private static final int max = 100;

    // ReentrantLock对象
    private static final ReentrantLock lock = new ReentrantLock();

    private static class Print implements Runnable {

        private final int index;

        public Print(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (Print.class) {
                    if (count >= 101) {
                        return;
                    }
                    log.debug("Thread-{}:{}", index, count++);
                }
            }
        }
    }

    /**
     * 这段代码实现了三个线程按顺序循环打印数字。每个线程会轮流打印当前计数值，并在完成后将控制权交给下一个线程。具体来说：
     *
     * 线程0打印的数字是0, 3, 6, 9, ...
     * 线程1打印的数字是1, 4, 7, 10, ...
     * 线程2打印的数字是2, 5, 8, 11, ...
     */
    private static class Seq implements Runnable {
        private final int index;

        public Seq(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            // 当count小于最大值时循环执行
            while (count < max) {
                synchronized (LOCK) {
                    // 如果当前线程的index不等于count % 3，线程等待
                    while (count % 3 != index) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // 在打印之前再次检查count是否小于等于最大值
                    if (count <= max) {
                        log.debug("Thread-{}:{}", index, count);
                    }
                    // 增加count值
                    count++;
                    // 唤醒其他等待的线程
                    LOCK.notifyAll();
                }
            }
        }
    }


    /**
     * 这段代码实现了使用 ReentrantLock 和 Condition 类来协调多个线程按顺序执行的功能。具体来说，每个线程会按顺序打印当前计数值，并在完成后将控制权交给下一个线程。
     */
    private static class Worker extends Thread {

        // 线程的索引，用于决定该线程什么时候执行。
        private int index;

        // 包含了所有 Condition 对象的列表，用于线程之间的协调。
        private List<Condition> conditionList;

        public Worker(int index, List<Condition> conditionList) {
            super("Thread-" + index);
            this.index = index;
            this.conditionList = conditionList;
        }



        @Override
        public void run() {
            while (true) {
                // 获取锁，确保只有一个线程可以执行临界区代码
                lock.lock();
                try {
                    // 检查当前计数是否与线程索引匹配，如果不匹配，则当前线程等待
                    if (count % 3 != index) {
                        // 当前线程等待被唤醒
                        conditionList.get(index).await();
                    }

                    // 如果计数超过最大值，唤醒下一个线程并退出
                    if (count > max) {
                        nextSingal();
                        return;
                    }

                    // 打印当前线程索引和计数值
                    log.debug("Thread-{}:{}", index, count);
                    // 递增计数值
                    count++;
                    // 唤醒下一个线程
                    nextSingal();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    // 释放锁
                    lock.unlock();
                }
            }
        }

        /**
         * 计算下一个线程的索引，并唤醒对应的线程
         */
        private void nextSingal() {
            int nextIndex = (index + 1) % conditionList.size();
            conditionList.get(nextIndex).signal();
        }
    }

    /**
     * 这段代码通过创建多个线程来按顺序打印 count 的值。它使用了简单的忙等待（busy-waiting）机制来控制线程的执行顺序。每个线程在特定条件下打印并增加 count 的值。
     * 核心思想：yield自旋的方式，如果当前的值不需要被当前线程打印，那么就让出该线程。
     * 虽然这种方式简单，但在高并发环境下可能导致 CPU 资源浪费，因为线程会在忙等待中频繁切换。
     */
    private static class OtherWorker implements Runnable {

        // 线程的索引，用于决定该线程什么时候执行。
        private final int index;

        public OtherWorker(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            // 确保线程在 count 小于 max 时持续运行。
            while (count < max) {
                // 检查当前计数是否与线程索引匹配。如果不匹配，线程调用 Thread.yield()，让出CPU时间片，等待其他线程执行。
                while (count % 3 != index) {
                    Thread.yield();
                }

                // 如果计数超过最大值，线程退出。
                if (count > max) {
                    return;
                }

                // 打印当前线程索引和计数值。
                log.debug("Thread-{}:{}", index, count);
                // 递增计数值。
                count++;
            }
        }
    }

    /**
     * 这样写固然能通过锁来保证循环打印了1-100，但是却不能保证线程是按照顺序打印的，这个时候就需要用到线程的通信机制。
     */
    private static void test1() {
        for (int i = 0; i < 3; i++) {
            new Thread(new Print(i)).start();
        }
    }


    private static void test2() {
        for (int i = 0; i < 3; i++) {
            new Thread(new Seq(i)).start();
        }
    }

    private static void test3() {
        final List<Condition> conditionList = new ArrayList<>();
        for (int i = 0; i < loopNumber; i++) {
            Condition condition = lock.newCondition();
            conditionList.add(condition);
            Worker worker = new Worker(i,conditionList);
            worker.start();
        }
    }

    private static void test4() {
        for (int i = 0; i < loopNumber; i++) {
            new Thread(new OtherWorker(i)).start();
        }
    }

    public static void main(String[] args) {
        // 错误的示例：
        // test1();

        // synchronized实现
        // test2();

        // 使用ReentrantLock实现
        // test3();

        // 使用yield实现
        test4();

    }




}
