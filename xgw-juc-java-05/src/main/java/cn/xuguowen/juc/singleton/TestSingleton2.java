package cn.xuguowen.juc.singleton;

/**
 * ClassName: TestSingleton2
 * Package: cn.xuguowen.juc.singleton
 * Description:
 *
 * @Author 徐国文
 * @Create 2024/6/27 14:27
 * @Version 1.0
 */
// 问题1：枚举单例是如何限制实例个数的
// 回答：Java 枚举类型在定义时，枚举的每个实例都是静态的并且是 final 的。这意味着枚举类型在类加载时，JVM 会保证每个枚举实例只被实例化一次，并且是线程安全的。通过这种机制，枚举类型可以确保单例模式只会有一个实例。这里的 INSTANCE 枚举常量是 Singleton 枚举类型的唯一实例，因此实现了单例模式
// 问题2：枚举单例在创建时是否有并发问题
// 回答：没有并发问题。Java 枚举类型在类加载时被初始化，类加载过程由 JVM 保证线程安全。因此，枚举类型的单例在创建时不会有并发问题。
// 问题3：枚举单例能否被反射破坏单例
// 回答：枚举单例不能被反射破坏单例。Java 枚举类型的实现限制了反射的使用。尝试使用反射创建枚举实例时，会抛出 IllegalArgumentException。这是因为枚举类型的构造器是隐式的，并且枚举类型不允许外部通过反射创建新的实例。
// 问题4：枚举单例能否被反序列化破坏单例
// 回答：枚举单例不能被反序列化破坏单例。Java 的枚举类型在反序列化时有特殊处理，保证反序列化的枚举实例与预定义的枚举实例相同。即使枚举实例被序列化，然后反序列化，反序列化返回的也是相同的枚举实例。
// 问题5：枚举单例属于懒汉式还是饿汉式
// 回答：枚举单例属于饿汉式单例。枚举类型在类加载时即被实例化，确保单例实例在类加载时就被创建。
// 问题6：枚举单例如果希望加入一些单例创建时的初始化逻辑该如何做
// 回答：可以在枚举类型的构造函数中加入初始化逻辑。枚举类型的构造函数是私有的，并且在类加载时执行。因此，可以通过这种方式加入初始化逻辑。
public enum TestSingleton2 {
    // 其实枚举就是静态常量，翻译成字节码文件看 public final static enum Lcn/xuguowen/juc/singleton/TestSingleton2; INSTANCE
    INSTANCE;

    TestSingleton2() {
        // 初始化逻辑
        System.out.println("Singleton instance is being initialized.");
    }
}
