package cn.xuguowen.juc.thread_pool_method;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ClassName: TestInvokeAll
 * Package: cn.xuguowen.juc.thread_pool_method
 * Description:测试没有时限的invokeAll()方法
 *
 * @Author 徐国文
 * @Create 2024/7/18 13:27
 * @Version 1.0
 */
@Slf4j(topic = "c.TestInvokeAll")
public class TestInvokeAll {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        List<Future<String>> futureList = executorService.invokeAll(Arrays.asList(
                () -> {
                    log.debug(" task1 begin");
                    Sleeper.sleep(1);
                    return "1";
                },
                () -> {
                    log.debug(" task2 begin");
                    Sleeper.sleep(0.5);
                    return "2";
                },
                () -> {
                    log.debug(" task3 begin");
                    Sleeper.sleep(2);
                    return "3";
                }
        ));


        // 线程池中的所有线程执行完毕之后，主线程获取结果。主线程这里必须等待2.5s之后方可获取到结果，因为线程池使用的是固定大小的线程池，只有2个核心线程。
        futureList.forEach((future) -> {
            try {
                log.debug(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
