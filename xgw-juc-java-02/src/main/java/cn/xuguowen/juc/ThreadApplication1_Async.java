package cn.xuguowen.juc;

import cn.xuguowen.juc.util.FileReader;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadApplicatiin1_Async
 * Package: cn.xuguowen.juc
 * Description:线程应用之异步调用。
 * 1.站在方法调用的角度上看待异步和同步：
 *  同步：需要等待结果返回，才能继续运行就是同步。
 *  异步：不需要等待结果返回，就能继续运行就是异步。
 * 2.设计：
 *  多线程可以让方法执行变为异步的（即不要巴巴干等着）比如说读取磁盘文件时，假设读取操作花费了 5 秒钟，如果没有线程调度机制，这 5 秒 cpu 什么都做不了，其它代码都得暂停...
 * 3.结论：
 *  ① 比如在项目中，视频文件需要转换格式等操作比较费时，这时开一个新线程处理视频转换，避免阻塞主线程
 *  ② tomcat 的异步 servlet 也是类似的目的，让用户线程处理耗时较长的操作，避免阻塞 tomcat 的工作线程
 *
 *
 * @Author 徐国文
 * @Create 2024/5/15 11:47
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadApplication1_Async")
public class ThreadApplication1_Async {

    public static final String PATH = ThreadApplication1_Async.class.getClassLoader().getResource("logback.xml").getPath();

    public static void main(String[] args) {
        // 同步
        // sync();

        // 异步
        async();
    }

    public static void sync() {
        FileReader.read(PATH);
        log.info("do other things");
    }

    public static void async() {
        new Thread(() ->FileReader.read(PATH)).start();
        log.info("do other things");
    }
}
