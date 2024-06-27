package cn.xuguowen.juc.singleton;

/**
 * ClassName: TestSingleton4
 * Package: cn.xuguowen.juc.singleton
 * Description:
 *
 * @Author 徐国文
 * @Create 2024/6/27 14:46
 * @Version 1.0
 */
public class TestSingleton4 {

    private TestSingleton4() { }
    // 问题1：解释为什么要加 volatile ?
    // 可见性：volatile 关键字保证了所有线程都能看到 INSTANCE 的最新状态。当一个线程修改 INSTANCE 时，其他线程能够立即看到这个修改。
    // 禁止指令重排序：在没有 volatile 的情况下，编译器和处理器可能会对指令进行重排序，导致 INSTANCE 被部分初始化的对象引用被其他线程读取。volatile 关键字确保了在对象被完全构建之前，不会发布到其他线程。
    private static volatile TestSingleton4 INSTANCE = null;

    // 问题2：对比实现3, 说出这样做的意义
    // 这种双重检查锁定（Double-Checked Locking）机制的实现提高了性能。
    // 1.减少不必要的同步：在 INSTANCE 已经初始化的情况下，避免了每次调用 getInstance() 都进行同步操作，减少了同步带来的开销。
    // 2.提高性能：在高并发环境下，减少了 synchronized 带来的性能瓶颈。只有在 INSTANCE 为 null 时，才会进入同步块进行实例化。
    public static TestSingleton4 getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (TestSingleton4.class) {  // t2线程获取到锁资源
            // 问题3：为什么还要在这里加为空判断, 之前不是判断过了吗
            if (INSTANCE != null) {            // 如果没有这次的判断，t2线程就会再创建一个实例对象出来。
                return INSTANCE;
            }
            INSTANCE = new TestSingleton4();   // t1线程创建完对象释放锁资源
            return INSTANCE;
        }
    }

}
