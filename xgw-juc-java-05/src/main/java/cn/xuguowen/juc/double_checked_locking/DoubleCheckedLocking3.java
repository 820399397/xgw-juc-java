package cn.xuguowen.juc.double_checked_locking;

/**
 * ClassName: DoubleCheckedLockingTest1
 * Package: cn.xuguowen.juc.double_checked_locking
 * Description:1.对DoubleCheckedLocking3的改正：使用volatile关键字修饰共享变量
 *
 * @Author 徐国文
 * @Create 2024/6/26 13:39
 * @Version 1.0
 */
public final class DoubleCheckedLocking3 {

    private DoubleCheckedLocking3() {
    }

    public static volatile DoubleCheckedLocking3 INSTANCE = null;

    /**
     * 这段代码反编译后的jvm指令如下：
     *        // -------------------------------------> 加入对 INSTANCE 变量的读屏障
     *        0: getstatic     #2                  // Field INSTANCE:Lcn/xuguowen/juc/double_checked_locking/DoubleCheckedLocking3;
     *        3: ifnonnull     37
     *        6: ldc           #3                  // class cn/xuguowen/juc/double_checked_locking/DoubleCheckedLocking3
     *        8: dup
     *        9: astore_0
     *       10: monitorenter  -----------------------> 保证原子性、可见性
     *       11: getstatic     #2                  // Field INSTANCE:Lcn/xuguowen/juc/double_checked_locking/DoubleCheckedLocking3;
     *       14: ifnonnull     27
     *       17: new           #3                  // class cn/xuguowen/juc/double_checked_locking/DoubleCheckedLocking3
     *       20: dup
     *       21: invokespecial #4                  // Method "<init>":()V
     *       24: putstatic     #2                  // Field INSTANCE:Lcn/xuguowen/juc/double_checked_locking/DoubleCheckedLocking3;
     *       //  -------------------------------------> 加入对 INSTANCE 变量的写屏障
     *       27: aload_0
     *       28: monitorexit  -----------------------> 保证原子性、可见性
     *       29: goto          37
     *       32: astore_1
     *       33: aload_0
     *       34: monitorexit
     *       35: aload_1
     * 其中:
     *  - 17 表示创建对象，将对象引用入栈
     *  - 20 表示复制一份对象引用
     *  - 21 表示利用一个对象引用，调用构造方法
     *  - 24 表示利用一个对象引用，赋值给 static INSTANCE
     * 由于INSTANCE并没有完全被synchronized控制起来，所以这里的有序性是无法保证的。也许 jvm 会优化为：先执行 24，再执行 21。
     * 比如有2个线程：t1 和 t2
     * 这时 t1 还未完全将构造方法执行完毕，如果在构造方法中要执行很多初始化操作，那么 t2 拿到的是将是一个未初始化完毕的单例。
     * 解决方式：对 INSTANCE 使用 volatile 修饰即可，可以禁用指令重排，但要注意在 JDK 5 以上的版本的 volatile 才会真正有效
     * 从字节码上看不出来volatile指令的效果。但是我们可以根据volatile原理进行分析
     * @return
     */
    public static DoubleCheckedLocking3 getInstance() {
        // 针对于有序性：读屏障会确保指令重排序时，不会将读屏障之后的代码排在读屏障之前
        // 加入读屏障，从代码上不太容易看。从字节码的角度看，也就是说不会先执行24 在执行21的。
        if (INSTANCE == null) {
            synchronized (DoubleCheckedLocking3.class) {
                if (INSTANCE == null) {
                    // 针对于有序性：写屏障会确保指令重排序时，不会将写屏障之前的代码排在写屏障之后。
                    // 加入写屏障，从代码上不太容易看。从字节码的角度看，也就是说不会先执行24 在执行21的。
                    INSTANCE = new DoubleCheckedLocking3();
                }
            }
        }
        return INSTANCE;
    }
}
