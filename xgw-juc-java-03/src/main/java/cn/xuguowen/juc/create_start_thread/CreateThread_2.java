package cn.xuguowen.juc.create_start_thread;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: CreateThread_2
 * Package: cn.xuguowen.juc.create_start_thread
 * Description:使用 Runnable 配合 Thread：把【线程】和【任务】分开
 *
 * @Author 徐国文
 * @Create 2024/5/16 12:10
 * @Version 1.0
 */
@Slf4j(topic = "c.CreateThread_2")
public class CreateThread_2 {

    public static void main(String[] args) {
        // 匿名内部类的方式创建任务
        Runnable task = new Runnable() {
            @Override
            public void run() {
               log.debug("running...");
            }
        };

        // 创建线程,把任务丢进去
        // 原理：还是调用的是Thread类中的run方法，但是会判断Runnable类型的target成员变量是否为空。如果不为空，则调用Runnable接口实现类的run方法
        // 这里体现了静态代理的设计模式
        Thread thread = new Thread(task,"t1");

        // 启动线程
        thread.start();
    }

    /**
     * 匿名内部类的方式创建任务等同于如下代码：
     * class MyRunnable implements Runnable {
     *     public void run() {
     *         // 线程要执行的任务
     *     }
     * }
     *
     * // 创建Runnable对象
     * Runnable myRunnable = new MyRunnable();
     *
     * // 将Runnable对象传递给Thread构造函数
     * Thread thread = new Thread(myRunnable);
     *
     * // 启动线程
     * thread.start();
     */
}

