package cn.xuguowen.juc.daemon_thread;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: DaemonThread
 * Package: cn.xuguowen.juc.daemon_thread
 * Description:主线程与守护线程:默认情况下，Java 进程需要等待所有线程都运行结束，才会结束。有一种特殊的线程叫做守护线程，只要其它非守护线程运行结束了，即使守护线程的代码没有执行完，也会强制结束。
 * 这里的主线程不仅涵盖main线程，也包括其他的工作线程。
 * 1.演示Java的进程中，只要存在非守护线程没有结束，则进程就不会结束。
 * 2.知晓一些守护线程：垃圾回收器线程就是一种守护线程；Tomcat 中的 Acceptor 和 Poller 线程都是守护线程，所以 Tomcat 接收到 shutdown 命令后，不会等待它们处理完当前请求
 *
 * @Author 徐国文
 * @Create 2024/5/22 20:44
 * @Version 1.0
 */
@Slf4j(topic = "c.DaemonThread")
public class DaemonThread {

    public static void main(String[] args) {
        test3();
    }

    /**
     * 在 Java 中，如果一个守护线程（daemon thread）创建了一个新的线程，那么这个新线程也是一个守护线程。这是 Java 线程模型的默认行为。
     */
    private static void test3() {
        // 外部线程
        Thread daemonThread = new Thread(() -> {
            log.debug("Daemon thread started.");

            // 内部线程
            Thread innerThread = new Thread(() -> {
                while (true) {
                    log.debug("Inner thread running.");
                    Sleeper.sleep(1);
                }
            });
            innerThread.start();

            // 外部线程睡眠2s
            Sleeper.sleep(2);
            log.debug("Daemon thread finished.");
        });

        daemonThread.setDaemon(true);
        daemonThread.start();

        Sleeper.sleep(2);

        log.debug("Main thread finished.");
    }

    /**
     * 演示Java的进程中，只要所有的非守护线程结束了，进程也就结束了。即使有守护线程还在运行，也不管他了，进行直接结束。
     */
    private static void test2() {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            log.debug("结束运行...");
        }, "t1");
        t1.setDaemon(true);
        t1.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.debug("main thread over...");
    }

    /**
     * main线程虽然结束了，但是因为还有非守护线程在运行，所以进程不会结束。
     */
    private static void test1() {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            log.debug("结束运行...");
        }, "t1");
        t1.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        t1.interrupt();
        log.debug("main thread over...");
    }


}
