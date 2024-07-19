package cn.xuguowen.juc.thread_pool_method;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ClassName: TestShutdown
 * Package: cn.xuguowen.juc.thread_pool_method
 * Description:测试线程池的shutdown()方法
 * 1.shutdown()方法：线程池状态变为 SHUTDOWN；阻塞队列不会接收新任务；但是已提交的任务会执行完毕；此方法不会阻塞调用线程的执行
 * 2.shutdownNow()方法：线程池状态变为 STOP；阻塞队列不会接收新任务；并返回等待执行任务的列表；并用 interrupt 的方式中断正在执行的任务
 *
 * @Author 徐国文
 * @Create 2024/7/19 12:46
 * @Version 1.0
 */
@Slf4j(topic = "c.TestShutdown")
public class TestShutdown {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<String> f1 = executorService.submit(() -> {
            log.debug(" task1 begin");
            Sleeper.sleep(1);
            log.debug(" task1 end");
            return "1";
        });

        Future<String> f2 = executorService.submit(() -> {
            log.debug(" task2 begin");
            Sleeper.sleep(0.5);
            log.debug(" task2 end");
            return "2";
        });

        Future<String> f3 = executorService.submit(() -> {
            log.debug(" task3 begin");
            Sleeper.sleep(2);
            log.debug(" task3 end");
            return "3";
        });

        // 主线程调用线程池的shutdown()方法
        log.debug("主线程调用shutdown()方法！");
        // executorService.shutdown();
        // 如下的这个方法不建议使用：因为你根本不知道线程池中的线程具体什么时候结束。可以使用Future.get()方法来等待任务执行完成。
        // executorService.awaitTermination(2, TimeUnit.SECONDS);

        List<Runnable> runnables = executorService.shutdownNow();
        log.debug("主线程不会阻塞住，会继续执行的！未执行的任务列表：{},f3:{}",runnables,f3);
    }
}
