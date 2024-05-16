package cn.xuguowen.juc.create_start_thread;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: CreateThread1
 * Package: cn.xuguowen.juc.create_start_thread
 * Description:直接使用Thread创建线程
 *
 * @Author 徐国文
 * @Create 2024/5/16 11:03
 * @Version 1.0
 */
@Slf4j(topic = "c.CreateThread_1")
public class CreateThread_1 {

    public static void main(String[] args) {
        test1();
    }

    /**
     * 匿名内部类通常用于创建那些只需要一次性使用的类的实例，尤其是在接口或抽象类的实现中.
     * 使用匿名内部类的方式创建线程：匿名内部类继承了Thread类，并重写了run()方法.
     * 原理：匿名内部类作为Thread的子类，重写了run()方法，因此调用的是子类中的run()方法
     *
     */
    private static void test1() {
        // 建议：给每个线程都起个名字
        Thread thread = new Thread("t1") {
            @Override
            public void run() {
                // 要执行的任务
                log.debug("线程正在执行...");
            }
        };
        // 启动线程
        thread.start();
        log.debug("主线程继续执行...");
    }

    /**
     * 匿名内部类创建线程的方式等同于如下代码：
     * class MyThread extends Thread {
     *     public void run() {
     *         // 线程要执行的任务
     *     }
     * }
     *
     * // 创建并启动线程
     * MyThread thread = new MyThread();
     * thread.start();
     */
}
