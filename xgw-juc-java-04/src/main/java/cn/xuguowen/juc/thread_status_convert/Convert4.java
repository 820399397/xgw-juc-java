package cn.xuguowen.juc.thread_status_convert;

/**
 * ClassName: Convert4
 * Package: cn.xuguowen.juc.thread_status_convert
 * Description:重新理解线程状态之间的转换：根据图来 ./img/线程状态转换图.png
 * 9.RUNNABLE <--> BLOCKED
 *  - t 线程用 synchronized(obj) 获取了对象锁时如果竞争失败，从 RUNNABLE --> BLOCKED
 *  - 持 obj 锁线程的同步代码块执行完毕，会唤醒该对象上所有 BLOCKED 的线程重新竞争，如果其中 t 线程竞争成功，从 BLOCKED --> RUNNABLE ，其它失败的线程仍然 BLOCKED
 *
 * @Author 徐国文
 * @Create 2024/6/7 20:18
 * @Version 1.0
 */
public class Convert4 {
}
