package cn.xuguowen.juc.thread_design_pattren;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: TestToPhaseTermination
 * Package: cn.xuguowen.juc.thread_design_pattren
 * Description:两阶段终止模式：主要用来优雅（给其一个料理后事的机会）的停止其他线程。
 * &#064;See  xgw-juc-java-03/src/main/java/cn/xuguowen/juc/thread_design_pattren/TestToPhaseTermination.java
 * 之前是采用打断标记的方式实现的。现在采用标记为的方式去实现，但是会涉及到volatile关键字的使用。
 *
 * @Author 徐国文
 * @Create 2024/6/20 12:46
 * @Version 1.0
 */
public class TestToPhaseTermination {
    public static void main(String[] args) {
        ToPhaseTermination tpt = new ToPhaseTermination();
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
@Slf4j(topic = "c.ToPhaseTermination")
class ToPhaseTermination {
    // 监控线程
    private Thread monitorThread;

    // 默认是false，没有被打断
    // 为什么要用volatile关键字修饰：为了确保stop在多个线程之间保持可见性。
    // 目前程序存在main线程和监控线程monitorThread。main线程调用tpt.stop();方法将stop的值改为true，要确保监控线程对其stop是可见的，所以需要volatile修饰
    private volatile boolean stop;

    // 启动监控线程
    public void start() {
        monitorThread = new Thread(() -> {
            while (true) {
                if (stop) {
                    log.debug("监控线程被终止，终止标记为:{},执行料理后事的逻辑！",stop);
                    break;
                }
                try {
                    Thread.sleep(1000);     // 情况1：在sleep期间（其他线程将stop标记置为true，此时等待睡醒了之后，又执行了一次监控，然后进入下一次循环，直接就退出了）。
                                                 // 如果要做的更好的话：就是打断睡眠，不再进行下一次的监控，直接就退出了。这种做法就不需要再catch语句块中重新设置打断标记为true了。因为我们目前用的是停止标记stop变量
                    // 每个1s对系统进行一次监控
                    log.debug("执行监控...");      // 情况2：在执行监控期间（其他线程将stop标记置为true，所以下一次循环的时候，直接就退出了）
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 在 catch 块中调用 currentThread.interrupt() 方法的目的是重新设置当前线程的打断标记为 true。这样做的原因是，当 InterruptedException 被捕获并处理时，该异常会清除线程的打断标记，将其重新设置为 false。
                    // 因此，为了确保线程在下一次循环中能够正确退出循环，需要显式地将线程的打断标记重新设置为 true。
                    // 通过在 catch 块中调用 currentThread.interrupt() 方法，实际上是在告诉当前线程：“我已经处理了 InterruptedException，现在我要重新打断你，以便你能够在下一次循环中检测到这个打断标记，并正确地退出循环。”
                    // currentThread.interrupt();
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