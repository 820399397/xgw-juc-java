package cn.xuguowen.juc.thread_status;

import cn.xuguowen.juc.utils.FileReader;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Test1
 * Package: cn.xuguowen.juc.thread_status
 * Description:演示操作系统层面的阻塞状态，在java层面中是出于runnable状态的。因为在java里面是无法区分的。
 *
 * @Author 徐国文
 * @Create 2024/5/22 21:08
 * @Version 1.0
 */
@Slf4j(topic = "c.Test1")
public class Test1 {

    public static void main(String[] args) {
        // 验证在java中，是无法分清操作系统层面的阻塞状态的，在java中认为操作系统层面的阻塞状态也是runnable状态。
        // 注意：debug界面是running状态，其实就是runnable状态
        Thread t1 = new Thread(() -> {
            // 底层使用的是阻塞API读取的：BIO.但是在java层面，认为当前这个线程还是处于runnable状态的。通过debug可以看到
            FileReader.read("C:\\Users\\Administrator\\Documents\\TencentMeeting\\2024-05-08 14.55.48 徐国文的快速会议 295804694\\meeting_01.mp4");
        }, "t1");
        t1.start();

        log.debug("main thread sout...");
    }
}
