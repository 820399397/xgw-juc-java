package cn.xuguowen.juc.synchronized_principle;

/**
 * ClassName: Synchronized_Principle
 * Package: cn.xuguowen.juc.synchronized_principle
 * Description:synchronzied原理：从2个角度理解：文字描述角度、jvm字节码指令角度。要结合Monitor的原理进行理解。
 * 1.文字角度：
 * synchronized 是 Java 中的一个很重要的关键字，主要用来加锁。synchronized 的使用方法比较简单，主要可以用来修饰方法和代码块。根据其锁定的对象不同，可以用来定义同步方法和同步代码块。
 * 方法级的同步是隐式的（同步方法）。同步方法的常量池中会有一个 ACC_SYNCHRONIZED 标志。当某个线程要访问某个方法的时候，会检查是否有 ACC_SYNCHRONIZED，如果有设置，则需要先获得监视器锁，
 * 然后开始执行方法，方法执行之后再释放监视器锁。这时如果其他线程来请求执行方法，会因为无法获得监视器锁而被阻断住。
 * 值得注意的是，如果在方法执行过程中，发生了异常，并且方法内部并没有处理该异常，那么在异常被抛到方法外面之前监视器锁会被自动释放。
 *
 * 同步代码块使用 monitorenter 和 monitorexit 两个指令实现。 可以把执行 monitorenter 指令理解为加锁，执行 monitorexit 理解为释放锁。
 * 每个对象维护一个记录着被锁次数的计数器。未被锁定的对象的该计数器为 0，当一个线程获得锁（执行 monitorenter ）后，该计数器自增变为 1 ，当同一个线程再次获得该对象的锁的时候，计数器再次自增。
 * 当同一个线程释放锁（执行 monitorexit 指令）的时候，计数器再自减。当计数器为 0 的时候。锁将被释放，其他线程便可以获得锁。
 *
 * synchronized 所添加的锁有以下几个特点：
 *  - 互斥性 ：同一时间点，只有一个线程可以获得锁，获得锁的线程才可以处理被 synchronized 修饰的代码片段。
 *  - 阻塞性 ：只有获得锁的线程才可以执行被 synchronized 修饰的代码片段，未获得锁的线程只能阻塞，等待锁释放。
 *  - 可重入性 ：如果一个线程已经获得锁，在锁未释放之前，再次请求锁的时候，是必然可以获得锁的。
 *
 * 2.字节码角度理解：观察如下main方法的字节码
 *  0 getstatic #2 <cn/xuguowen/juc/synchronized_principle/Synchronized_Principle.lock : Ljava/lang/Object;>
 *  3 dup
 *  4 astore_1
 *  5 monitorenter
 *  6 getstatic #3 <cn/xuguowen/juc/synchronized_principle/Synchronized_Principle.counter : I>
 *  9 iconst_1
 * 10 iadd
 * 11 putstatic #3 <cn/xuguowen/juc/synchronized_principle/Synchronized_Principle.counter : I>
 * 14 aload_1
 * 15 monitorexit
 * 16 goto 24 (+8)
 * 19 astore_2
 * 20 aload_1
 * 21 monitorexit
 * 22 aload_2
 * 23 athrow
 * 24 return
 *
 * Exception Table 异常表
 * 0	6	16	19	cp_info #0
 * 1	19	22	19	cp_info #0
 *
 * 注意：方法级别的 synchronized 不会在字节码指令中有所体现。有一个 ACC_SYNCHRONIZED 标志
 *
 *
 * @Author 徐国文
 * @Create 2024/5/29 14:02
 * @Version 1.0
 */
public class Synchronized_Principle {

    static final Object lock = new Object();
    static int counter = 0;


    public static void main(String[] args) {
        synchronized (lock) {
            counter++;
        }
    }

    /*
    在下面的同步方法和同步代码块的例子中，均提供了两个代码 demo，分别是两种类型的锁，即类锁和对象锁。
    区分方式按照其锁定的内容进行划分。对象锁锁定的内容是对象，类锁锁定的内容是类。其实，类锁也是通过对象锁实现的，因为在 Java 中，万物皆对象。
    无论是同步方法还是同步代码块，其实现其实都要依赖对象的监视器（Monitor）。
     */

    // 同步方法 类锁
    private synchronized static void test1() {

    }

    // 同步方法 对象锁
    private synchronized void test2() {

    }

    // 同步代码块 类锁
    private void test3() {
        synchronized (Synchronized_Principle.class) {
            System.out.println("hello world!");
        }
    }

    // 同步代码块 对象锁
    private void test4() {
        synchronized (this) {
            System.out.println("hello world!");
        }
    }

}
