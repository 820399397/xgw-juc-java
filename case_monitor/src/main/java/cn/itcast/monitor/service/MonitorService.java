package cn.itcast.monitor.service;

import cn.itcast.monitor.controller.MonitorController;
import cn.itcast.monitor.vo.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author yihang
 */
@Service
@Slf4j
public class MonitorService {

    // 监控线程是否停止的标记
    private volatile boolean stop;

    // 监控线程是否启动多次的标记。强制要求只允许启动一次。
    // 为何这里的标记使用volatile关键字修饰？而xgw-juc-java-05/src/main/java/cn/xuguowen/juc/thread_design_pattren/TestBalking.java中的start变量没有用volatile关键字修饰呢？
    // 而且的区别就在于：
    // 当前项目是一个web项目，每一次的请求都是从tomcat中的线程池获取一个线程出来处理请求的。前端页面多次点击start按钮就是tomcat的多个线程请求，所以需要使用volatile关键字保证变量对其他线程是可见的。
    // 而另一个项目中只有main线程对其读写，所以不需要保证可见性
    private volatile boolean starting;

    // 监控线程
    private Thread monitorThread;

    /**
     * 犹豫模式的使用
     * 1.先验证监控线程是否已经启动起来了，如果启动起来了，就不需要再次启动了
     */
    public void start() {
        // 缩小同步范围，提升性能
        synchronized (this) {
            log.info("该监控线程已启动?({})", starting);
            if (starting) {
                return;
            }
            starting = true;
        }

        // 由于之前的 balking 模式，以下代码只可能被一个线程执行，因此无需互斥
        monitorThread = new Thread(() -> {
            while (!stop) {
                report();
                sleep(2);
            }
            // 这里的监控线程只可能启动一个，因此只需要用 volatile 保证 starting 的可见性
            log.info("监控线程已停止...");
            starting = false;
        },"监控线程");

        stop = false;
        log.info("监控线程已启动...");
        monitorThread.start();
    }

    private void report() {
        Info info = new Info();
        info.setTotal(Runtime.getRuntime().totalMemory());
        info.setFree(Runtime.getRuntime().freeMemory());
        info.setMax(Runtime.getRuntime().maxMemory());
        info.setTime(System.currentTimeMillis());

        // 尝试将Info对象添加到队列中
        MonitorController.QUEUE.offer(info);
    }

    private void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
        }
    }

    public synchronized void stop() {
        stop = true;
        // 不加打断需要等到下一次 sleep 结束才能退出循环，这里是为了更快结束
        monitorThread.interrupt();
    }

}
