package cn.xuguowen.juc.thread_method;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadMethod_Start
 * Package: cn.xuguowen.juc.thread_method
 * Description:线程的start()方法
 * 1.调用start()方法来启动线程，而非run()方法。
 * 2.体会start()方法对线程状态的影响
 * 3.多次调用start()方法会抛出异常。
 *
 * @Author 徐国文
 * @Create 2024/5/18 20:32
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadMethod_StartAndRun")
public class ThreadMethod_StartAndRun {

    public static void main(String[] args) {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                log.debug("running...");
            }
        };
        t1.setName("t1");
        System.out.println(t1.getState());  // NEW
        t1.start();
        // 多次调用start()方法会出现IllegalThreadStateException异常
        // t1.start();
        System.out.println(t1.getState());  // RUNNABLE


        // 或许你有这样的疑问：启动线程就是在执行线程内的run()方法，那为何不直接调用run()方法呢？
        // 因为直接调用run()方法，就相当于在main线程里执行run()方法，并没有启动新的线程！
        // console out：20:35:27 [main] c.ThreadMethod_StartAndRun - running...
        // t1.run();

    }
}
