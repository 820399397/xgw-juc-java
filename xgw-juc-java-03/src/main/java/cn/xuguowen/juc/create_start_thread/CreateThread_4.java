package cn.xuguowen.juc.create_start_thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * ClassName: CreateThread_4
 * Package: cn.xuguowen.juc.create_start_thread
 * Description:
 *
 * @Author 徐国文
 * @Create 2024/5/16 12:24
 * @Version 1.0
 */
@Slf4j(topic = "c.CreateThread_4")
public class CreateThread_4 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // test1();

        // 将Callable对象传递给FutureTask
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            log.debug("running……");
            Thread.sleep(2000);
            // 线程要执行的任务，并返回一个结果
            return "任务执行完成";
        });

        // 创建并启动线程
        Thread thread = new Thread(futureTask,"t1");
        thread.start();

        // 主线程阻塞，获取线程执行结果
        String result = futureTask.get();
        log.debug("结果: {}", result);
    }


    private static void test1() throws InterruptedException, ExecutionException {
        // 与Runnable接口不同，Callable接口允许线程执行一个任务并返回一个结果，而不像Runnable只能执行一个任务而不能返回结果。
        Callable callable = () -> {
            // 线程要执行的任务，并返回一个结果
            return "任务执行完成";
        };

        // 将Callable对象传递给FutureTask
        FutureTask<String> futureTask = new FutureTask<>(callable);

        // 创建并启动线程
        Thread thread = new Thread(futureTask,"t1");
        thread.start();

        // 获取线程执行结果
        String result = futureTask.get();
        log.debug("结果: {}", result);
    }
}
