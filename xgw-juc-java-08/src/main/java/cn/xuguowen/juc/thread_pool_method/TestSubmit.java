package cn.xuguowen.juc.thread_pool_method;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ClassName: TestSubmit
 * Package: cn.xuguowen.juc.thread_pool_method
 * Description:测试submit()方法。
 *
 * @Author 徐国文
 * @Create 2024/7/18 12:24
 * @Version 1.0
 */
@Slf4j(topic = "c.TestSubmit")
public class TestSubmit {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<String> future = executorService.submit(() -> {
            log.debug("hello submit method!");
            Sleeper.sleep(1);
            return "hello submit method";
        });

        // 主线程阻塞等待获取结果.feture.get()方法其实这里就用到了之前的保护性暂停模式
        // @see xgw-juc-java-04.cn.xuguowen.juc.pattern
        String result = future.get();
        log.debug("result: {}", result);
    }

}
