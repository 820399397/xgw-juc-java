package cn.xuguowen.juc.singleton;

import java.io.Serializable;

/**
 * ClassName: TestSingleton1
 * Package: cn.xuguowen.juc.singleton
 * Description:线程安全单例习题：单例模式有很多实现方法，饿汉、懒汉、静态内部类、枚举类，试分析每种实现下获取单例对象（即调用getInstance）时的线程安全，并思考注释中的问题
 * - 饿汉式：类加载就会导致该单实例对象被创建
 * - 懒汉式：类加载不会导致该单实例对象被创建，而是首次使用该对象时才会创建
 *
 * @Author 徐国文
 * @Create 2024/6/27 14:12
 * @Version 1.0
 */
// 问题1：为什么加 final:为了防止类被继承，子类覆盖其行为，破坏其行为，导致获取到的实例不是单例的。其次如果允许继承，子类可能会有自己的实例，这也破坏了单例模式
// 问题2：如果实现了序列化接口, 还要做什么来防止反序列化破坏单例.只要在TestSingleton1类中定义readResolve()就可以解决该问题。
public final class TestSingleton1 implements Serializable {

    private static final long serialVersionUID = 6953113673405449422L;

    // 问题3：为什么设置为私有? 是否能防止反射创建新的实例?
    // 将构造函数设为 private 是为了防止类的外部代码直接创建实例。这样只能通过类内部的机制（如 getInstance 方法）来创建实例。
    // 这并不能完全防止反射攻击。反射可以访问私有构造函数并创建新的实例。为了防止这种情况，可以在构造函数中添加逻辑，抛出异常以防止反射调用：
    private TestSingleton1() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized.");
        }
    }

    // 问题4：这样初始化是否能保证单例对象创建时的线程安全?
    // 通过将单例实例声明为 static final，并在声明时进行初始化，利用了 Java 类加载机制的特性。
    // 类加载机制保证了类的静态初始化块（包括静态变量的初始化）在类加载时由 JVM 保证线程安全。因此，这种初始化方式是线程安全的。
    private static final TestSingleton1 INSTANCE = new TestSingleton1();

    // 问题5：为什么提供静态方法而不是直接将 INSTANCE 设置为 public, 说出你知道的理由
    // 1.延迟初始化：在某些单例实现中（如懒汉式单例），可以通过 getInstance 方法实现延迟初始化，即只有在需要时才创建实例。虽然这里用的是饿汉式单例，但保留方法接口有助于未来的修改和扩展。
    // 2.封装：通过静态方法，可以控制实例的创建过程，并在需要时增加额外的逻辑（如日志记录、性能监控、异常处理等）。直接暴露 INSTANCE 则缺少这种灵活性。
    // 3.一致性：使用方法访问实例是一种更好的编程习惯，使得接口更加一致且易于理解和使用。
    // 4.反序列化保护：如前所述，通过 readResolve 方法，可以防止反序列化破坏单例。如果直接暴露 INSTANCE，无法实现这种保护。
    public static TestSingleton1 getInstance() {
        return INSTANCE;
    }

    // 防止反序列化破坏单例
    private Object readResolve() {
        return INSTANCE;
    }

}
