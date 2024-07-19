package cn.xuguowen.juc.thread_pool_method;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName: TestInvokeAny
 * Package: cn.xuguowen.juc.thread_pool_method
 * Description:测试invokeAny()方法：提交 tasks 中所有任务，哪个任务先成功执行完毕，返回此任务执行结果，其它任务取消
 *
 * @Author 徐国文
 * @Create 2024/7/19 12:41
 * @Version 1.0
 */
@Slf4j(topic = "c.TestInvokeAny")
public class TestInvokeAny {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        String result = executorService.invokeAny(Arrays.asList(
                () -> {
                    log.debug(" task1 begin");
                    Sleeper.sleep(1);
                    log.debug(" task1 end");
                    return "1";
                },
                () -> {
                    log.debug(" task2 begin");
                    Sleeper.sleep(0.5);
                    log.debug(" task2 end");
                    return "2";
                },
                () -> {
                    log.debug(" task3 begin");
                    Sleeper.sleep(2);
                    log.debug(" task3 end");
                    return "3";
                }
        ));

        log.debug("result:{}", result);
    }
}
