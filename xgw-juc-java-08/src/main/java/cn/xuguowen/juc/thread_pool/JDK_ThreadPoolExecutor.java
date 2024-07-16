package cn.xuguowen.juc.thread_pool;

/**
 * ClassName: JDK_ThreadPoolExecutor
 * Package: cn.xuguowen.juc.thread_pool
 * Description:JDK内部提供的线程池技术介绍：
 * 1.线程池状态:ThreadPoolExecutor 使用 int 的高 3 位来表示线程池状态，低 29 位表示线程数量.
 *  状态名       高3位   接收新任务    处理阻塞队列任务       说明
 *  RUNNING     111     Y           Y
 *  SHUTDOWN    000     N           Y                   不会接收新任务，但会处理阻塞队列剩余任务
 *  STOP        001     N           N                   会中断正在执行的任务，并抛弃阻塞队列任务
 *  TIDYING     010     -           -                   任务全执行完毕，活动线程为 0 即将进入终结
 *  TERMINATED  011     -           -                   终结状态
 * 从数字上比较，TERMINATED > TIDYING > STOP > SHUTDOWN > RUNNING。说明：第一位是1 表示是负数。
 *
 * 疑问：为何不用两个int。一个记录线程池的状态，另一个记录线程数量。
 * 解答：在ThreadPoolExecutor源码中，有这样一个成员变量：private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
 *      ctl是一个原子整数，它打包了两个概念字段：
 *      workerCount：表示线程的有效数量
 *      runState：表示线程池的运行状态：是否关闭、正在运行等
 *      目的是将线程池状态与线程个数合二为一，这样就可以用一次 cas 原子操作进行赋值。
 *      // c 为旧值， ctlOf 返回结果为新值
 *      ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))));
 *      // rs 为高 3 位代表线程池状态， wc 为低 29 位代表线程个数，ctl 是合并它们
 *      private static int ctlOf(int rs, int wc) { return rs | wc; }
 *
 * 2.构造方法
 *  public ThreadPoolExecutor(int corePoolSize,
 *                               int maximumPoolSize,
 *                               long keepAliveTime,
 *                               TimeUnit unit,
 *                               BlockingQueue<Runnable> workQueue,
 *                               ThreadFactory threadFactory,
 *                               RejectedExecutionHandler handler) {}
 *  - corePoolSize 核心线程数目 (最多保留的线程数)
 *  - maximumPoolSize 最大线程数目：核心线程数+救急线程数=最大线程数。补充：JDK内部的线程池中有2中线程：核心线程和救急线程
 *  - keepAliveTime 生存时间 - 针对救急线程
 *  - unit 时间单位 - 针对救急线程
 *  - workQueue 阻塞队列：那就是核心线程用完了，新的任务首先就会加入到这个阻塞队列中排队等待
 *  - threadFactory 线程工厂 - 创建线程对象，可以为线程创建时起个好名字。为了方便调试
 *  - handler 拒绝策略。不会立刻执行，JDK内置的线程池会优先采用救急线程去执行任务。如果救急线程也被占用了，那此时核心线程占用，队列满了，救急线程也被占用，才会采取相对应的拒绝策略
 *  关于救急线程的说明：救急线程的存在前提是：如果队列选择了有界（容量限制）队列，那么当核心线程用完了，任务也超过了队列的大小时，会创建 maximumPoolSize - corePoolSize 数目的线程来救急。
 *                  救急线程和核心线程的区别就是它本身有生存时间这个概念的。当救急线程执行完任务之后，救急线程就会被销毁掉，变成一个结束的状态。
 *                  核心线程执行完任务之后，不会结束，仍然保存在线程池中的，让这个核心线程一直处于运行状态。
 *
 * 3.构造方法中的参数影响着将来线程池的行为，所以非常重要。所以如下是对线程池参数的影响描述：
 *  - 线程池中刚开始没有线程，当一个任务提交给线程池后，线程池会创建一个新线程来执行任务。懒惰创建和加载的
 *  - 当线程数达到 corePoolSize 并没有线程空闲，这时再加入任务，新加的任务会被加入workQueue 队列排队，直到有空闲的线程，才会执行队列中的任务。
 *  - 如果队列选择了有界队列，那么任务超过了队列大小时，会创建 maximumPoolSize - corePoolSize 数目的线程来救急。当高峰过去后，超过corePoolSize 的救急线程如果一段时间没有任务做，需要结束节省资源，这个时间由keepAliveTime 和 unit 来控制。
 *  - 如果线程到达 maximumPoolSize 仍然有新任务这时会执行拒绝策略。拒绝策略 jdk 提供了 4 种实现：
 *      - AbortPolicy 让调用者抛出 RejectedExecutionException 异常，这是默认策略
 *      - CallerRunsPolicy 让调用者运行任务
 *      - DiscardPolicy 放弃本次任务
 *      - DiscardOldestPolicy 放弃队列中最早的任务，本任务取而代之
 *    其它著名框架也提供了实现
 *      - Dubbo 的实现，在抛出 RejectedExecutionException 异常之前会记录日志，并 dump 线程栈信息，方便定位问题
 *      - Netty 的实现，是创建一个新线程来执行任务
 *      - ActiveMQ 的实现，带超时等待（60s）尝试放入队列，类似我们之前自定义的拒绝策略
 *      - PinPoint 的实现，它使用了一个拒绝策略链，会逐一尝试策略链中每种拒绝策略
 *
 *
 *
 * @Author 徐国文
 * @Create 2024/7/16 16:09
 * @Version 1.0
 */
public class JDK_ThreadPoolExecutor {
    public static void main(String[] args) {

    }
}
