package cn.xuguowen.juc.create_start_thread;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: CreateThread_3
 * Package: cn.xuguowen.juc.create_start_thread
 * Description:Lambda表达式的方式创建线程
 *
 * @Author 徐国文
 * @Create 2024/5/16 12:16
 * @Version 1.0
 */
@Slf4j(topic = "c.CreateThread_3")
public class CreateThread_3 {
    public static void main(String[] args) {
        // test1();

        test2();
    }

    private static void test2() {
        Thread thread = new Thread(() -> log.debug("running..."));
        thread.start();
    }


    private static void test1() {
        Runnable runnable = () -> log.debug("running...");

        Thread thread = new Thread(runnable, "t1");
        thread.start();
    }
}
