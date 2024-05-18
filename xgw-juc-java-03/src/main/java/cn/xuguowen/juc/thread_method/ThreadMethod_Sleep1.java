package cn.xuguowen.juc.thread_method;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadMethod_Sleep1
 * Package: cn.xuguowen.juc.thread_method
 * Description:sleep()方法
 * 1.体会线程sleep()调用之前和之后对线程的状态影响
 *
 * @Author 徐国文
 * @Create 2024/5/18 20:48
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadMethod_Sleep1")
public class ThreadMethod_Sleep1 {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t1");
        t1.start();

        // 20:50:27 [main] c.ThreadMethod_Sleep1 - thread t1 status：RUNNABLE
        log.debug("thread t1 status：{}", t1.getState());

        Thread.sleep(500);

        // 20:50:28 [main] c.ThreadMethod_Sleep1 - thread t1 status：TIMED_WAITING
        log.debug("thread t1 status：{}", t1.getState());
    }
}
