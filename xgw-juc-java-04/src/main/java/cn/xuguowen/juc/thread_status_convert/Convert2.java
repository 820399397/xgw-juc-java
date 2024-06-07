package cn.xuguowen.juc.thread_status_convert;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Convert2
 * Package: cn.xuguowen.juc.thread_status_convert
 * Description:重新理解线程状态之间的转换：根据图来 ./img/线程状态转换图.png
 * 2.RUNNABLE <--> WAITING:t1线程用 synchronized(obj) 获取了对象锁后
 *  - 调用 obj.wait() 方法时，t1 线程从 RUNNABLE --> WAITING
 *  - 调用 obj.notify() ， obj.notifyAll() ， t.interrupt() 时
 *      - 竞争锁成功，t1 线程从 WAITING --> RUNNABLE
 *      - 竞争锁失败，t 线程从 WAITING --> BLOCKED(EntryList)
 *
 * 3.RUNNABLE <--> WAITING：当前线程调用 t.join() 方法时，当前线程从 RUNNABLE --> WAITING
 *   注意：谁调用的这个方法，谁就是当前线程。当前线程在t 线程对象的监视器上等待
 *   t 线程运行结束，或调用了当前线程的 interrupt() 时，当前线程从 WAITING --> RUNNABLE
 *
 * 4.RUNNABLE <--> WAITING
 *   当前线程调用 LockSupport.park() 方法会让当前线程从 RUNNABLE --> WAITING
 *   调用 LockSupport.unpark(目标线程) 或调用了线程 的 interrupt() ，会让目标线程从 WAITING --> RUNNABLE
 *
 *
 * @Author 徐国文
 * @Create 2024/6/7 20:05
 * @Version 1.0
 */
@Slf4j(topic = "c.Convert2")
public class Convert2 {

    static final Object obj = new Object();

    /**
     * 1.t1 和 t2 线程都进入WAITING状态，通过debug界面查看。注意：debug界面是wait状态，等同于waiting
     * 2.当主线程往下走1步，走到大括号的结束处，我主线程虽然全部唤醒了其他线程，但是我还没有释放锁资源，所以此时t1和t2进入了EntryList中，变成了Block状态。
     *  debug界面显示的是Monitor
     * 3.当主线程释放锁资源后，t1和t2竞争锁资源，竞争成功，进入RUNNABLE状态，debug界面显示的是RUNNING状态。
     *
     * @param args
     */
    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 如下代码打断点
                log.debug("其它代码...."); // 断点
            }
        },"t1").start();

        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 如下代码打断点
                log.debug("其它代码...."); // 断点
            }
        },"t2").start();

        Sleeper.sleep(0.5);
        log.debug("唤醒 obj 上其它线程");
        synchronized (obj) {
            // 如下代码打断点
            obj.notifyAll(); // 唤醒obj上所有等待线程 断点
        }
    }
}
