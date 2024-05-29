package cn.xuguowen.juc.synchronized_principle;

/**
 * ClassName: JAVA_Object_Components
 * Package: cn.xuguowen.juc.synchronized_principle
 * Description:Java中对象的组成部分:
 *  1.对象的头部（Header）：包含一些必要的信息，如哈希码、垃圾回收信息、锁信息等。
 *  2.实例数据（Instance Data）：对象的实例变量，也就是我们通常所说的成员变量。
 *  3.对齐填充（Padding）：为了保证对象的大小是8字节的倍数，可能需要添加一些没有意义的字节来对齐内存地址。
 *  4.方法表（Method Table）：如果是一个类对象，还会包含指向方法区中方法表的引用，用于动态分派（即多态）。
 *  注意：不同的操作系统（32位/64位）和虚拟机实现可能会有所不同。
 *
 * 其中：对象头（64 bits）部通常包含以下信息：
 *  1.标记字（Mark Word）(32 bits)
 *      - 哈希码（Hash Code）：用于快速比较对象是否相等的一个标识。如果两个对象的哈希码不相等，那它们一定不相等；如果哈希码相等，它们可能相等，需要通过 equals() 方法进一步比较。
 *      - GC 分代年龄（Garbage Collection Generation Age）：用于判断对象是否需要被垃圾回收。
 *      - 锁信息：用于支持对象的同步和并发操作，包括偏向锁、轻量级锁、重量级锁等相关信息。
 *  2.类指针（Class Pointer）(32 bits)指向对象所属类的元数据信息，用于在运行时确定对象的类型，支持多态特性。
 *
 * 其中：数组对象的对象头部比较特殊（96 bits）
 *  1.标记字（Mark Word）(32 bits)
 *  2.类指针（Class Pointer）(32 bits)
 *  3.数组长度（Array Length）(32 bits)
 *
 *
 *
 * @Author 徐国文
 * @Create 2024/5/29 13:20
 * @Version 1.0
 */
public class JAVA_Object_Components {
    public static void main(String[] args) {

    }
}
