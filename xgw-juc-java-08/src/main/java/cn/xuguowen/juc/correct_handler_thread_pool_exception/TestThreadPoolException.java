package cn.xuguowen.juc.correct_handler_thread_pool_exception;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * ClassName: TestThreadPoolException
 * Package: cn.xuguowen.juc.correct_handler_thread_pool_exception
 * Description:如何正确处理线程池内部出现的异常呢？
 * 1.线程池内部主动捕获异常
 * 2.配合 callable 和 futrue.get() 获取线程池内的异常信息
 *
 * @Author 徐国文
 * @Create 2024/7/24 12:20
 * @Version 1.0
 */
@Slf4j(topic = "c.TestThreadPoolException")
public class TestThreadPoolException {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // test1();

        test2();
    }

    /**
     * 配合 callable 和 futrue 实现。
     * future的get方法不仅会获取到正确的结果，也会获取到异常信息。
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void test2() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Future<Boolean> future = executorService.submit(() -> {
            log.debug("task1……");
            int i = 1 / 0;
            return true;
        });

        log.error("线程池运行的结果是：{}",future.get());
    }

    /**
     * 线程池内部自己捕获异常
     */
    private static void test1() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

        executor.schedule(() -> {
            log.debug("task1……");
            try {
                int i = 1 / 0;
            } catch (Exception e){
                log.error("发生异常……{}",e.getMessage(),e);
            }
        },1, TimeUnit.SECONDS);

    }
}
