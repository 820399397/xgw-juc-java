package cn.xuguowen.juc.thread_pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName: TestNewFixedThreadPool
 * Package: cn.xuguowen.juc.thread_pool
 * Description:测试newFixedThreadPool（固定容量大小的线程池）的使用:线程池中的线程数量是固定的，创建时指定一个正整数。无论有多少任务提交到线程池中，线程池中运行的线程数量始终保持不变。如果所有线程都在忙，新的任务将进入队列等待执行。
 *
 * 方法内部的源码：
 * 参数：nThreads：线程池中线程的数量。必须是正整数，如果传入的参数小于等于0，会抛出IllegalArgumentException。
 * public static ExecutorService newFixedThreadPool(int nThreads) {
 *      return new ThreadPoolExecutor(nThreads, nThreads,
 *                                0L, TimeUnit.MILLISECONDS,
 *                                new LinkedBlockingQueue<Runnable>());
 * }
 *
 * 特点：
 * 1.线程池中的线程数量不会动态变化。固定大小的线程池适用于负载较为恒定的场景，避免了线程频繁创建和销毁的开销。
 * 2.当所有线程都在忙时，新的任务会被放入队列中等待执行。默认使用LinkedBlockingQueue作为任务队列。阻塞队列是无界的，可以放任意数量的任务
 * 3.线程池中的线程会被重用，减少了线程创建和销毁的开销，提高了性能。
 * 4.线程池会自动管理线程的生命周期，包括线程的创建、调度和销毁。
 * 5.核心线程数 == 最大线程数（没有救急线程被创建），因此也无需针对救急线程设置生存时间
 *
 * 使用场景；适用于任务量已知，相对耗时的任务
 *
 * @Author 徐国文
 * @Create 2024/7/17 12:21
 * @Version 1.0
 */
@Slf4j(topic = "c.TestNewFixedThreadPool")
public class TestNewFixedThreadPool {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        executorService.execute(() -> {
            log.info("1");
            // 前提条件：nThreads参数是1.也就是线程池中只有一个核心线程。
            // 这里体现出一个特性：和newSingleThreadExecutor()方法创建出来的线程特点是一样的。出现异常后，会再新建一个线程去执行剩下的任务
            int i = 1 / 0;
        });

        executorService.execute(() -> {
            log.info("2");
        });

        executorService.execute(() -> {
            log.info("3");
        });
    }
}
