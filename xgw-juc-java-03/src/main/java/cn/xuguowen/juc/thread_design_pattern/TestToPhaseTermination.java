package cn.xuguowen.juc.thread_design_pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ToPhaseTerminationTest
 * Package: cn.xuguowen.juc.thread_design_pattern
 * Description:两阶段终止模式：主要用来优雅（给其一个料理后事的机会）的停止其他线程。
 * 停止线程的错误方式：
 *  ① 使用线程对象的 stop() 方法停止线程：
 *      stop 方法会真正杀死线程，如果这时线程锁住了共享资源，那么当它被杀死后就再也没有机会释放锁，其它线程将永远无法获取锁。
 *  ② 使用 System.exit(int) 方法停止线程：
 *      目的仅是停止一个线程，但这种做法会让整个程序都停止。
 *
 * @Author 徐国文
 * @Create 2024/5/21 12:25
 * @Version 1.0
 */
@Slf4j(topic = "c.TestToPhaseTermination")
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

    // 启动监控线程
    public void start() {
        monitorThread = new Thread(() -> {
            Thread currentThread = Thread.currentThread();
            while (true) {
                boolean interruptedFlag = Thread.currentThread().isInterrupted();
                if (interruptedFlag) {
                    log.debug("监控线程被打断，打断标记为:{},执行料理后事的逻辑！",interruptedFlag);
                    break;
                }
                try {
                    Thread.sleep(1000);     // 情况1：在sleep期间被打断（其他线程打断正在阻塞的线程，线程的打断标记会被清除掉，所以需要在catch块中将打断标记设置为true，下一次循环的时候，直接就退出了）
                    // 每个1s对系统进行一次监控
                    log.debug("执行监控...");      // 情况2：在执行监控期间被打断（其他线程打断正在运行的一个线程，线程的打断标记是true，所以下一次循环的时候，直接就退出了）
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 在 catch 块中调用 currentThread.interrupt() 方法的目的是重新设置当前线程的打断标记为 true。这样做的原因是，当 InterruptedException 被捕获并处理时，该异常会清除线程的打断标记，将其重新设置为 false。
                    // 因此，为了确保线程在下一次循环中能够正确退出循环，需要显式地将线程的打断标记重新设置为 true。
                    // 通过在 catch 块中调用 currentThread.interrupt() 方法，实际上是在告诉当前线程：“我已经处理了 InterruptedException，现在我要重新打断你，以便你能够在下一次循环中检测到这个打断标记，并正确地退出循环。”
                    currentThread.interrupt();
                }
            }
        },"monitorThread");
        monitorThread.start();
    }

    // 停止监控线程
    public void stop() {
        monitorThread.interrupt();
    }
}
