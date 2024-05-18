package cn.xuguowen.juc.thread_method;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadMethod_Yield
 * Package: cn.xuguowen.juc.thread_method
 * Description:yield()方法
 * 1.调用 yield 会让当前线程从 Running 进入 Runnable 就绪状态，然后调度执行其它线程.具体的实现依赖于操作系统的任务调度器
 *   就是说：有可能存在让不出去的情况，就是任务调度器还会执行当前线程。时间片还是会分给一个处于Runnable状态的线程，但是一定不会分给处于TIMED_WAITING状态的线程
 *
 * @Author 徐国文
 * @Create 2024/5/18 21:04
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadMethod_Yield")
public class ThreadMethod_Yield {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            Thread.yield();
        }, "t1");
        log.debug("t1 thread status:{}", t1.getState());
        t1.start();
        log.debug("t1 thread status:{}", t1.getState());
    }
}
