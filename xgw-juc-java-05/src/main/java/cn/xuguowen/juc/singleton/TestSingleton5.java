package cn.xuguowen.juc.singleton;

/**
 * ClassName: TestSingleton5
 * Package: cn.xuguowen.juc.singleton
 * Description:
 *
 * @Author 徐国文
 * @Create 2024/6/27 14:57
 * @Version 1.0
 */
public class TestSingleton5 {

    private TestSingleton5() { }
    // 问题1：属于懒汉式还是饿汉式
    // 这种实现方式属于懒汉式（Lazy Initialization）。实例 INSTANCE 是在第一次调用 getInstance() 方法时才创建的，而不是在类加载时创建。
    private static class LazyHolder {
        static final TestSingleton5 INSTANCE = new TestSingleton5();
    }
    // 问题2：在创建时是否有并发问题：在创建时没有并发问题
    // 类加载机制：LazyHolder 类是在第一次调用 getInstance() 方法时被加载的。Java 类加载机制保证了类加载过程是线程安全的。也就是说，类加载过程由 JVM 保证线程安全，不会出现并发问题。
    // 静态初始化：在类加载过程中，LazyHolder 类的静态变量 INSTANCE 会被初始化。这是由类加载机制保证的，且静态变量的初始化是线程安全的。
    public static TestSingleton5 getInstance() {
        // jvm类的加载是按需加载的，什么时候用到什么时候加载
        return LazyHolder.INSTANCE;
    }

}
