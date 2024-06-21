package cn.xuguowen.juc.thread_design_pattren;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: TestBalking
 * Package: cn.xuguowen.juc.thread_design_pattren
 * Description:同步模式之 Balking:Balking （犹豫）模式用在一个线程发现另一个线程或本线程已经做了某一件相同的事，那么本线程就无需再做了，直接结束返回.
 * 为什么引出来犹豫模式呢？是因为我们前面使用volatile实现的两阶段终止模式中，存在一个缺点：就是可以多次调用start()方法，启动多个监控线程。
 *                     但是一般情况下，系统中的监控线程只有一个就够了，所以我们使用Balking模式来优化前面的两阶段终止模式。
 *
 * @Author 徐国文
 * @Create 2024/6/21 12:22
 * @Version 1.0
 */
public class TestBalking {

    public static void main(String[] args) {
        ToPhaseTermination1 tpt = new ToPhaseTermination1();
        tpt.start();
        // 多次调用start()方法，就会启动多个监控线程.这就是问题所在。所以需要使用balking犹豫模式去优化
        tpt.start();
        tpt.start();

        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        tpt.stop();
    }


}

// 比如是后台的一个监控任务对象
@Slf4j(topic = "c.ToPhaseTermination1")
class ToPhaseTermination1 {
    // 监控线程
    private Thread monitorThread;

    // 默认是false，没有被打断
    // 为什么要用volatile关键字修饰：为了确保stop在多个线程之间保持可见性。
    // 目前程序存在main线程和监控线程monitorThread。main线程调用tpt.stop();方法将stop的值改为true，要确保监控线程对其stop是可见的，所以需要volatile修饰
    private volatile boolean stop;

    // 监控线程是否已经启动了.只有主线程对其进行改变，所以不需要使用volatile关键字修饰保证可见性。
    private boolean start = false;

    // 启动监控线程
    public void start() {
        synchronized (this) {
            if (start) {
                // 如果进入if语句，说明已经启动过了，无需再启动监控线程了
                return;
            }
            start = true;
        }

        monitorThread = new Thread(() -> {
            while (true) {
                if (stop) {
                    log.debug("监控线程被终止，终止标记为:{},执行料理后事的逻辑！",stop);
                    break;
                }
                try {
                    Thread.sleep(1000);
                    log.debug("执行监控...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"monitorThread");
        monitorThread.start();
    }

    // 停止监控线程
    public void stop() {
        this.stop = true;
        // 1.着重理解情况1;
        // 2.注释如下代码和放开如下代码，注意看控制台的输出结果，体会这细微的差别
        monitorThread.interrupt();
    }
}
