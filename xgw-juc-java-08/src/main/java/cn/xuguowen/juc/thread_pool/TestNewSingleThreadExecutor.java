package cn.xuguowen.juc.thread_pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName: TestNewSingleThreadExecutor
 * Package: cn.xuguowen.juc.thread_pool
 * Description:测试NewSingleThreadExecutor（单线程执行器）线程池：用于创建一个单线程执行器。这个执行器在整个生命周期内只会使用唯一一个工作线程来执行所有提交的任务，保证所有任务按顺序执行。如果这个线程异常终止，会创建一个新的线程来继续执行任务。
 * 方法源码：
 * public static ExecutorService newSingleThreadExecutor() {
 *         return new FinalizableDelegatedExecutorService
 *             (new ThreadPoolExecutor(1, 1,
 *                                     0L, TimeUnit.MILLISECONDS,
 *                                     new LinkedBlockingQueue<Runnable>()));
 * }
 *
 * 特点总结：
 * - 简化并发控制：因为只有一个线程，所以不需要复杂的同步机制来控制任务的执行顺序。
 * - 线程安全：所有任务在同一个线程中执行，避免了多线程并发带来的线程安全问题
 * - 顺序执行：确保任务按照提交的顺序执行，适合需要严格执行顺序的场景。
 *
 * 注意事项：
 * - 性能限制：由于只有一个线程，不能并行处理多个任务，对于需要高并发的场景性能会受限。
 * - 阻塞问题：如果一个任务长时间阻塞，会导致后续任务无法及时执行。
 *
 * 使用场景：希望多个任务排队执行。
 *
 * newSingleThreadExecutor() 和 newFixedThreadPool(1) 方法创建的线程池几乎相同，区别在于：
 * Executors.newSingleThreadExecutor() 线程个数始终为1，不能修改。
 *  FinalizableDelegatedExecutorService 应用的是装饰器模式，只对外暴露了 ExecutorService 接口，因此不能调用 ThreadPoolExecutor 中特有的方法
 * Executors.newFixedThreadPool(1) 初始时为1，以后还可以修改。
 *  对外暴露的是 ThreadPoolExecutor 对象，可以强转后调用 setCorePoolSize 等方法进行修改
 *
 * @Author 徐国文
 * @Create 2024/7/17 13:51
 * @Version 1.0
 */
@Slf4j(topic = "c.TestNewSingleThreadExecutor")
public class TestNewSingleThreadExecutor {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // 1.注意观察控制台输出的线程名称信息：发现始终只有一个线程。都是名为pool-1-thread-1的线程在执行任务
        // 2.注意观察任务执行的顺序：始终是按照任务提交的顺序依次执行。
        executorService.execute(() -> {
            log.debug("1");
            // 自己创建一个单线程串行执行任务，如果任务执行失败而终止那么没有任何补救措施，而线程池还会新建一个线程，保证池的正常工作
            // 这里出现了异常：剩下的代码执行都是pool-1-thread-2线程去执行的
            int i = 1 / 0;
        });

        executorService.execute(() -> {
            log.debug("2");
        });

        executorService.execute(() -> {
            log.debug("3");
        });
    }
}
