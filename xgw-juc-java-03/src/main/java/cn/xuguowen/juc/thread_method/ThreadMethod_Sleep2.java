package cn.xuguowen.juc.thread_method;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadMethod_Sleep2
 * Package: cn.xuguowen.juc.thread_method
 * Description:sleep()方法
 * 2.体会正在睡眠中的线程被其他线程打断
 *
 * @Author 徐国文
 * @Create 2024/5/18 20:50
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadMethod_Sleep2")
public class ThreadMethod_Sleep2 {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("thread t1 running...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.debug("thread t1 interrupted...");
                throw new RuntimeException(e);
            }
        }, "t1");
        t1.start();

        Thread.sleep(1000);
        //主线程打断t1线程
        log.debug("main thread interrupt t1");
        t1.interrupt();
    }
}
