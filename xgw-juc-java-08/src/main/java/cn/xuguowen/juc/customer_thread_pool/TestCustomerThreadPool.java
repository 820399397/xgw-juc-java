package cn.xuguowen.juc.customer_thread_pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: TestCustomerThreadPool
 * Package: cn.xuguo.wen.juc.customer_thread_pool
 * Description:测试自定义线程池
 *
 * @Author 徐国文
 * @Create 2024/7/12 15:45
 * @Version 1.0
 */
@Slf4j(topic = "c.TestCustomerThreadPool")
public class TestCustomerThreadPool {

    public static void main(String[] args) {
        // test1();

        // test2();

        test3();

    }

    private static void test3() {
        // 线程池中的核心线程数是2，任务队列的容量是10
        CustomerThreadPool customerThreadPool = new CustomerThreadPool(1, 1000, TimeUnit.MICROSECONDS, 1,((taskQueue, task) -> {
            // 1.死等策略
            // taskQueue.put(task);

            // 2.带超时等待策略1
            // taskQueue.offer(500, TimeUnit.MICROSECONDS,task);

            // 2.带超时等待策略2
            // taskQueue.offer(1500, TimeUnit.MICROSECONDS,task);

            // 3.调用者放弃任务执行
            // log.debug("放弃执行任务：{}", task);

            // 4.调用者抛出异常
            // throw new RuntimeException(String.format("调用者抛出异常：%s",task));

            // 5.调用者自己执行任务:其实就是主线程自己执行任务
            task.run();
        }));

        for (int i = 0; i < 3; i++) {
            int j = i;
            customerThreadPool.execute(() -> {
                try {
                    // 测试死等策略时，打开如下代码
                    // Thread.sleep(1000000L);

                    // 测试带超时等待策略1时，打开如下代码
                    // Thread.sleep(1000L);

                    // 测试带超时等待策略2时，打开如下代码
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("{}",j);
            });
        }

    }

    /**
     * 测试任务队列中任务已经满了的情况。存在部分任务是等待加入任务队列中
     */
    private static void test2() {
        // 线程池中的核心线程数是2，任务队列的容量是10
        CustomerThreadPool customerThreadPool = new CustomerThreadPool(2, 1000, TimeUnit.MICROSECONDS, 10);

        for (int i = 0; i < 15; i++) {
            int j = i;
            customerThreadPool.execute(() -> {
                try {
                    Thread.sleep(1000000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.debug("{}",j);
            });
        }
    }

    /**
     * 测试正常情况：2个核心线程执行完毕之后从任务队列中获取任务继续执行
     */
    private static void test1() {
        // 线程池中的核心线程数是2，任务队列的容量是5
        CustomerThreadPool customerThreadPool = new CustomerThreadPool(2, 1000, TimeUnit.MICROSECONDS, 5);

        for (int i = 0; i < 5; i++) {
            int j = i;
            customerThreadPool.execute(() -> {
                log.debug("{}",j);
            });
        }
    }
}
