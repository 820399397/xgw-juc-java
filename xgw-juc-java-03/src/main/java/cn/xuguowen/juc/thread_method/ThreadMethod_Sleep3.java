package cn.xuguowen.juc.thread_method;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: ThreadMethod_Sleep3
 * Package: cn.xuguowen.juc.thread_method
 * Description:sleep()方法
 * 3.睡眠结束后的线程未必会立刻得到执行，还是得看操作系统的时间片
 * 4.建议用 TimeUnit 的 sleep 代替 Thread 的 sleep 来获得更好的可读性
 *
 * @Author 徐国文
 * @Create 2024/5/18 20:56
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadMethod_Sleep3")
public class ThreadMethod_Sleep3 {

    public static void main(String[] args) throws InterruptedException {
        log.debug("main thread running...");
        // 其实这个方法底层也是调用线程的sleep()方法，内部处理了时间换算的逻辑
        TimeUnit.SECONDS.sleep(1);
        log.debug("main thread run end...");
    }
}
