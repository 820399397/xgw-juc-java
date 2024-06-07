package cn.xuguowen.juc.thread_status_convert;

/**
 * ClassName: Convert3
 * Package: cn.xuguowen.juc.thread_status_convert
 * Description:重新理解线程状态之间的转换：根据图来 ./img/线程状态转换图.png
 * 5.RUNNABLE <--> TIMED_WAITING:t 线程用 synchronized(obj) 获取了对象锁后
 *  - 调用 obj.wait(long n) 方法时，t 线程从 RUNNABLE --> TIMED_WAITING
 *  - t 线程等待时间超过了 n 毫秒，或调用 obj.notify() ， obj.notifyAll() ， t.interrupt() 时
 *      - 竞争锁成功，t 线程从 TIMED_WAITING --> RUNNABLE
 *      - 竞争锁失败，t 线程从 TIMED_WAITING --> BLOCKED
 *
 * 6.RUNNABLE <--> TIMED_WAITING:
 *  - 当前线程调用 t.join(long n) 方法时，当前线程从 RUNNABLE --> TIMED_WAITING.注意是当前线程在t 线程对象的监视器上等待
 *  - 当前线程等待时间超过了 n 毫秒，或t 线程运行结束，或调用了当前线程的 interrupt() 时，当前线程TIMED_WAITING --> RUNNABLE
 *
 * 7.RUNNABLE <--> TIMED_WAITING
 *  - 当前线程调用 Thread.sleep(long n) ，当前线程从 RUNNABLE --> TIMED_WAITING
 *  - 当前线程等待时间超过了 n 毫秒，当前线程从 TIMED_WAITING --> RUNNABLE
 *
 * 8.RUNNABLE <--> TIMED_WAITING
 *   - 当前线程调用 LockSupport.parkNanos(long nanos) 或 LockSupport.parkUntil(long millis) 时，当前线程从 RUNNABLE --> TIMED_WAITING
 *   - 调用 LockSupport.unpark(目标线程) 或调用了线程 的 interrupt() ，或是等待超时，会让目标线程从TIMED_WAITING--> RUNNABLE
 *
 *
 * @Author 徐国文
 * @Create 2024/6/7 20:15
 * @Version 1.0
 */
public class Convert3 {
}
