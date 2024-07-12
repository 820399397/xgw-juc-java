package cn.xuguo.wen.juc.customer_thread_pool;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: CustomerThreadPool
 * Package: cn.xuguo.wen.juc.customer_thread_pool
 * Description:自定义线程池
 *
 * @Author 徐国文
 * @Create 2024/7/12 15:20
 * @Version 1.0
 */
@Slf4j(topic = "c.CustomerThreadPool")
public class CustomerThreadPool {
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;

    // 线程的个数
    private HashSet<Worker> workers = new HashSet();

    // 核心线程数
    private int coreSize;

    // 获取任务时的超时时间
    private long timeout;

    // 超时时间单位
    private TimeUnit unit;

    private RejectPolicy<Runnable> rejectPolicy;

    public CustomerThreadPool(int coreSize, long timeout, TimeUnit unit, int taskQueueCapacity) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.unit = unit;
        this.taskQueue = new BlockingQueue<>(taskQueueCapacity);
    }

    public CustomerThreadPool(int coreSize, long timeout, TimeUnit unit, int taskQueueCapacity,RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.unit = unit;
        this.taskQueue = new BlockingQueue<>(taskQueueCapacity);
        this.rejectPolicy = rejectPolicy;
    }

    /**
     * 执行任务
     *
     * @param task
     */
    public void execute(Runnable task) {
        // 当任务数没有超过 coreSize 时，直接交给 worker 对象执行
        // 如果任务数超过 coreSize 时，加入任务队列暂存
        synchronized (workers) {
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.debug("新增 worker{}, {}", worker, task);
                workers.add(worker);
                worker.start();
            } else {
                // 1.死等
                // taskQueue.put(task);
                // 2. 带超时等待
                // 3. 让调用者放弃任务执行
                // 4. 让调用者抛出异常
                // 5. 让调用者自己执行任务
                taskQueue.tryPut(rejectPolicy,task);
            }
        }
    }

    class Worker extends Thread {
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 执行任务
            // 1.当task不为null时，执行任务
            // 2.当 task 执行完毕，再接着从任务队列获取任务并执行
            // while (null != task || null != (task = taskQueue.take())) {  // 死等：当线程都执行完毕，任务队列BlockingQueue为空时，会一直等待生产者生产任务的
            while (null != task || null != (task = taskQueue.poll(timeout, unit))) {
                try {
                    log.debug("正在执行...{}", task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }

            synchronized (workers) {
                log.debug("worker 被移除{}", this);
                workers.remove(this);
            }
        }
    }
}
