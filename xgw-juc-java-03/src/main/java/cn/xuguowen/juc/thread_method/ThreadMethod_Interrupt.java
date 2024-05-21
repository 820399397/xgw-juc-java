package cn.xuguowen.juc.thread_method;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadMethod_Interrupt
 * Package: cn.xuguowen.juc.thread_method
 * Description:interrupt()
 *  1.主线程打断正在休眠的t1线程（阻塞状态）
 *  2.主线程打断正在运行的t1线程
 *  3.体会上述2种情况下，对打断标记的清除和设置
 * @Author 徐国文
 * @Create 2024/5/21 11:53
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadMethod_Interrupt")
public class ThreadMethod_Interrupt {

    public static void main(String[] args) {
        test2();
    }

    /**
     * 主线程打断正在运行的线程
     * 运行如下代码后发现：主线程打断t1线程，仅仅是通知t1线程我要干扰你，并不会直接将t1线程打断，具体t1线程什么时候结束运行，取决于人家自己。
     * 这个时候t1的打断标记没有被清除，也就是说打断标记是true，t1线程内部可以根据打断标记进行最后的处理工作。
     */
    private static void test2() {
        Thread t1 = new Thread(() -> {
            while (true) {
                boolean flag = Thread.currentThread().isInterrupted();
                if (flag) {
                    log.debug("t1 thread 后续的处理工作");
                    break;
                }
            }
        }, "t1");
        t1.start();

        log.debug("t1 thread interrupt...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        t1.interrupt();

    }


    /**
     * 主线程打断正在休眠的t1线程（阻塞状态）
     * 打断 sleep wait join 下的线程（也就是正在阻塞的线程），他们的打断标记会被清除，也就是置为false
     */
    private static void test1() {
        Thread t1 = new Thread(() -> {
            log.debug("t1 thread sleeping...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t1");
        t1.start();

        Sleeper.sleep(1);
        log.debug("main thread interrupt t1...");
        t1.interrupt();
        log.debug("t1 thread isInterrupted:{}",t1.isInterrupted());
    }
}
