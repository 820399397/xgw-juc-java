/**
 * ClassName: package-info
 * Package: cn.xuguowen.juc.thread_safe_class
 * Description:常见线程安全类
 * String
 * Integer:各种包装类
 * StringBuffer
 * Random
 * Hashtable
 * Vector
 * java.util.concurrent包下的类
 *
 * @See cn.xuguowen.juc.thread_safe_class.Test1
 * 注意：这里说它们是线程安全的是指，多个线程调用它们同一个实例的某个方法时，是线程安全的。也可以理解为它们的每个方法是原子的.
 * 但注意它们多个方法的组合不是原子的.
 *
 * 不可变类线程安全性:String、Integer 等都是不可变类，一旦创建，其状态（对象的字段值）就不能被修改的类。所有对不可变类对象的修改操作都会生成新的对象，而不是修改原对象。不可变类具有以下特点和优势：
 * 特点：
 *  - 状态不可变：对象一旦创建，其所有字段的值在对象的生命周期内都不会改变。
 *  - 所有字段是 final：类中的所有字段都是 final 类型的，这确保了字段的值只能在构造函数中被赋值一次，之后不能被修改。
 *  - 类是 final：通常类本身也会被声明为 final，这样子类就不能修改类的行为。
 *  - 没有 "setter" 方法：不提供修改字段值的 setter 方法，所有字段的值只能通过构造函数或静态工厂方法赋值。
 *  - 深度拷贝：如果类包含对可变对象的引用，如数组或集合，应确保这些引用不可变。这通常通过在构造函数中进行深度拷贝或返回不可变视图来实现。
 * 优势：
 *  - 线程安全：不可变对象天生具有线程安全性，因为它们的状态一旦初始化就不会改变，不需要同步控制就可以在多线程环境中安全共享。
 *  - 简单性：由于状态不可变，不可变对象的行为更加简单和直观，减少了对象状态的不一致性和复杂性。
 *  - 安全性：不可变对象在作为参数传递时，不用担心对象被修改，从而保证了数据的一致性和安全性。
 *  - 缓存和重用：不可变对象可以安全地缓存和重用，因为它们的状态不会变化。
 * @Author 徐国文
 * @Create 2024/5/26 16:45
 * @Version 1.0
 */
package cn.xuguowen.juc.thread_safe_class;