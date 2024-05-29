package cn.xuguowen.juc.synchronized_principle;

/**
 * ClassName: Monitor_Principle
 * Package: cn.xuguowen.juc.synchronized_principle
 * Description:Monitor 原理:被翻译为监视器或管程
 * 1.为了解决线程安全的问题，Java 提供了同步机制、互斥锁机制，这个机制保证了在同一时刻只有一个线程能访问共享资源。
 *   这个机制的保障来源于监视锁 Monitor，每个 Java 对象都拥有自己的监视锁 Monitor。当我们尝试获得对象的锁的时候，其实是对该对象拥有的 Monitor 进行操作。
 *   如果使用 synchronized 给对象上锁（重量级）之后，该对象头的Mark Word 中就被设置指向 Monitor 对象的指针。
 *
 * 2.什么是Monitor？
 * 先来举个例子：我们可以把监视器理解为包含一个特殊房间的建筑物，这个特殊房间同一时刻只能有一个客人（线程）。这个房间中包含了一些数据和代码。
 * 如果一个顾客想要进入这个特殊的房间，他首先需要在走廊（Entry Set）排队等待。调度器将基于某个标准（比如 FIFO）来选择排队的客户进入房间。
 * 如果，因为某些原因，该客户暂时因为其他事情无法脱身（线程被挂起），那么他将被送到另外一间专门用来等待的房间（Wait Set），这个房间的可以在稍后再次进入那件特殊的房间。
 * 如上面所说，这个建筑屋中一共有三个场所（Special Room 、Wait Room 、Hallway ）。
 * 总之，监视器是一个用来监视这些线程进入特殊的房间的。他的义务是保证（同一时间）只有一个线程可以访问被保护的数据和代码。
 * Monitor 其实是一种同步工具，也可以说是一种同步机制，它通常被描述为一个对象，主要特点是：
 *  - 对象的所有方法都被“互斥”的执行。好比一个 Monitor 只有一个运行“许可”，任一个线程进入任何一个方法都需要获得这个“许可”，离开时把许可归还。
 *  - 通常提供 信号（singal） 机制：允许正持有“许可”的线程暂时放弃“许可”，等待某个谓词成真（条件变量），而条件成立后，当前进程可以“通知”正在等待这个条件变量的线程，让他可以重新去获得运行许可。
 *
 * 3.Monitor的代码实现：在 Java 虚拟机(HotSpot)中，Monitor 是基于 C++ 实现的，由 ObjectMonitor 实现的，其主要数据结构如下：
 * ObjectMonitor() {
 *     _header       = NULL;
 *     _count        = 0;
 *     _waiters      = 0,
 *     _recursions   = 0;
 *     _object       = NULL;
 *     _owner        = NULL;
 *     _WaitSet      = NULL;
 *     _WaitSetLock  = 0 ;
 *     _Responsible  = NULL ;
 *     _succ         = NULL ;
 *     _cxq          = NULL ;
 *     FreeNext      = NULL ;
 *     _EntryList    = NULL ;
 *     _SpinFreq     = 0 ;
 *     _SpinClock    = 0 ;
 *     OwnerIsThread = 0 ;
 * }
 * 关键属性：
 * - _owner：指向持有 ObjectMonitor 对象的线程
 * - _WaitSet：存放处于 wait 状态的线程队列
 * - _EntryList：存放处于等待锁 block 状态的线程队列
 * - _recursions：锁的重入次数
 * - _count：用来记录该线程获取锁的次数
 *
 * 当多个线程同时访问一段同步代码时，首先会进入 _EntryList 队列中，当 Thread-1 执行 synchronized(obj) 就会将 Monitor 的所有者 Owner 置为 Thread-1，Monitor中只能有一个 Owner。
 * 同时 monitor 中的计数器 _count 加1。即获得对象锁。Thread-1 执行完同步代码块的内容，然后唤醒 EntryList 中等待的线程来竞争锁，竞争的时是非公平的
 * 若持有 monitor 的线程调用 wait() 方法，将释放当前持有的 monitor，_owner 变量恢复为 null，_count 自减 1，同时该线程进入 _WaitSet 集合中等待被唤醒。
 * 若当前线程执行完毕也将释放 monitor(锁)并复位变量的值，以便其他线程进入获取 monitor(锁)。
 * Monitor的异常机制异常机制也是存在的。即使发生了异常，也是会释放锁的（可以从synchronized的字节码角度查看验证）
 *
 * 4.参考文献：https://www.yuque.com/hollis666/hgtuok/gxq5p0#v0zgk
 *
 * @Author 徐国文
 * @Create 2024/5/29 13:32
 * @Version 1.0
 */
public class Monitor_Principle {
}
