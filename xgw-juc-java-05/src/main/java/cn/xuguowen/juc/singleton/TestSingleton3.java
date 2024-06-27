package cn.xuguowen.juc.singleton;

/**
 * ClassName: TestSingleton3
 * Package: cn.xuguowen.juc.singleton
 * Description:
 *
 * @Author 徐国文
 * @Create 2024/6/27 14:34
 * @Version 1.0
 */
public class TestSingleton3 {

    private TestSingleton3() { }

    private static TestSingleton3 INSTANCE = null;

    // 分析这里的线程安全, 并说明有什么缺点
    // 是否线程安全：getInstance() 方法被声明为 synchronized，因此在多线程环境中，只有一个线程可以同时访问这个方法。通过这种方式，可以确保单例实例的创建过程是线程安全的。
    // 缺点：
    // 1.性能问题：每次调用 getInstance() 方法时，都会进行同步操作。这会导致性能开销，尤其是在高并发环境下。当单例已经被创建后，每次获取实例都需要进行同步，这会影响系统性能。
    // 2.不必要的同步：在实例已经被创建后，同步操作实际上是不必要的，因为实例已经存在，没有竞争条件了。因此，这种方式会导致不必要的同步开销。
    public static synchronized TestSingleton3 getInstance() {
        if( INSTANCE != null ){
            return INSTANCE;
        }
        INSTANCE = new TestSingleton3();
        return INSTANCE;
    }
}
