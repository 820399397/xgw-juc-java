package cn.xuguo.wen.juc.customer_thread_pool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: BlockingQueue
 * Package: cn.xuguo.wen.juc.customer_thread_pool
 * Description:自定义的任务队列
 *
 * @Author 徐国文
 * @Create 2024/7/12 14:49
 * @Version 1.0
 */
@Slf4j(topic = "c.BlockingQueue")
public class BlockingQueue<T> {
    // 双端队列
    private Deque<T> taskDeque = new ArrayDeque<>();

    // 锁:因为认为在出队和入队的时候都需要加入锁，保证线程安全
    private ReentrantLock lock = new ReentrantLock();

    // 队列中没有任务了，执行任务的线程要等待
    private Condition emptyCondition = lock.newCondition();

    // 队列中的任务已经满了，生产者要等待
    private Condition fullCondition = lock.newCondition();

    // 任务队列的容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 带超时时限的任务阻塞出队
     * @param timeout
     * @param unit
     * @return
     */
    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            // 统一将时间转换为nanos
            long nanos = unit.toNanos(timeout);
            // 任务队列是空的，则需要等待
            while (taskDeque.isEmpty()) {
                try {
                    if (nanos <= 0) {
                        return null;
                    }
                    // awaitNanos方法内部解决了虚假唤醒的问题。
                    nanos = emptyCondition.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // 说明任务队列不再是空的了，队列中有任务了
            T task = taskDeque.removeFirst();
            //  唤醒等待的生产者线程
            fullCondition.signal();
            return task;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 任务阻塞出队
     *
     * @return
     */
    public T take() {
        lock.lock();
        try {
            // 任务队列是空的，则需要等待
            while (taskDeque.isEmpty()) {
                try {
                    emptyCondition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // 说明任务队列不再是空的了，队列中有任务了
            T task = taskDeque.removeFirst();
            //  唤醒等待的生产者线程
            fullCondition.signal();
            return task;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 任务阻塞入队
     *
     * @param task
     */
    public void put(T task) {
        lock.lock();
        try {
            // 任务队列满了，则需要等到
            while (taskDeque.size() == capacity) {
                try {
                    log.debug("等待加入任务队列：{} ...",task);
                    fullCondition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            log.debug("加入任务队列：{}",task);
            // 说明任务队列没有满，还可以容纳任务
            taskDeque.addLast(task);
            // 唤醒等待的消费者线程
            emptyCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带超时时限的任务阻塞入队
     * @param task
     * @return
     */
    public boolean offer(long timeout,TimeUnit unit,T task) {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            // 任务队列满了，则需要等到
            while (taskDeque.size() == capacity) {
                try {
                    if (nanos <= 0) {
                        return false;
                    }
                    log.debug("等待加入任务队列：{} ...",task);
                    nanos = fullCondition.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            log.debug("加入任务队列：{}",task);
            // 说明任务队列没有满，还可以容纳任务
            taskDeque.addLast(task);
            // 唤醒等待的消费者线程
            emptyCondition.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 尝试把任务加入任务队列
     * @param task
     */
    public void tryPut(RejectPolicy<T> rejectPolicy,T task) {
        lock.lock();
        try {
            // 判断队列是否已满
            if (taskDeque.size() == capacity) {
                // 队列已满，具体的处理策略由调用者自己决定
                rejectPolicy.reject(this,task);
            } else {
                log.debug("加入任务队列：{}",task);
                // 说明任务队列没有满，还可以容纳任务
                taskDeque.addLast(task);
                // 唤醒等待的消费者线程
                emptyCondition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return taskDeque.size();
        } finally {
            lock.unlock();
        }
    }



}
