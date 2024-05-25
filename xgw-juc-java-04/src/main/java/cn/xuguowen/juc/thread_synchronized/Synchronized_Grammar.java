package cn.xuguowen.juc.thread_synchronized;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Thread_Grammer
 * Package: cn.xuguowen.juc.thread_synchronized
 * Description:synchronized的语法
 *
 * @Author 徐国文
 * @Create 2024/5/25 10:29
 * @Version 1.0
 */
@Slf4j(topic = "c.Synchronized_Grammar")
public class Synchronized_Grammar {


    // 锁的对象是this
    public void increment1() {
        synchronized (this) {

        }
    }

    // 等价于
    public synchronized void increment2() {

    }


    // 锁的对象是Class类对象
    public static void increment3() {
        synchronized (Synchronized_Grammar.class) {

        }
    }

    // 等价于
    public static synchronized void increment4() {

    }

    // 我有一个疑问：static 和synchronized关键字在前在后有区别吗？
    // 如下这个方法IDEA已经给出警告：
    // Reorder the modifiers to comply with the Java Language Specification： 重新排序修饰符以符合Java语言规范
    // 无论哪种顺序，静态同步方法的锁是该类的Class对象。这意味着如果两个线程分别调用相同类的两个不同的静态同步方法，它们仍然会互相阻塞，因为它们都是在尝试获取同一个类级别的锁。
    // 因此，选择哪种顺序更多是代码风格的问题，没有功能上的差异。
    public synchronized static void increment5() {

    }

    // 不加 synchronzied 的方法就好比不遵守规则的人，不去老实排队（好比翻窗户进去的）
    public void increment6() {}


}
