package cn.xuguowen.juc.variables_thread_safe;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: Local_Variables3
 * Package: cn.xuguowen.juc.thread_safe
 * Description:演示局部变量引用的对象没有逃离方法作用范围:对Local_Variables2类的改进：使其变的线程安全。
 *
 * @Author 徐国文
 * @Create 2024/5/26 16:13
 * @Version 1.0
 */
@Slf4j(topic = "c.Local_Variables3")
public class Local_Variables3 {
    static final int THREAD_NUMBER = 2;
    static final int LOOP_NUMBER = 200;

    public static void main(String[] args) {
        // 1.这个对象是被多个线程共享的,但是此刻对象中是没有成员变量的，所以这个对象本身是线程安全的
        ThreadSafe5 test = new ThreadSafe5();
        // 2.启动多个线程，分别调用method1方法
        for (int i = 0; i < THREAD_NUMBER; i++) {
            new Thread(() -> {
                test.method1(LOOP_NUMBER);
            }, "Thread" + i).start();
        }
    }
}

/**
 * 这段代码是线程安全的，因为局部变量 list 是在每个线程的栈上独立创建和操作的。
 * - 局部变量的作用域和线程独立性：在 method1 方法中，ArrayList<String> list 是一个局部变量。每次 method1 被调用时，都会创建一个新的 ArrayList 实例，该实例仅在当前方法的作用范围内有效。
 * - 没有共享状态：ThreadUnsafe3 类中没有任何共享的成员变量，所有的操作都在局部变量上进行。由于 list 是局部变量，不会逃离方法的作用范围，因此不会被其他线程访问或修改。
 * - 局部变量本身是线程安全的，因为它们在每个线程的栈中独立存在，不会被其他线程访问。在这种情况下，即使 method2 和 method3 方法对 list 进行操作，这些操作也仅限于当前线程的局部变量，不会影响其他线程。
 * 说白了：就是堆内存中有2份List实例对象，两个线程各操作各的。
 */
@Slf4j(topic = "c.ThreadSafe3")
class ThreadSafe3 {

    public void method1(int loopNumber) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < loopNumber; i++) {
            // 临界区, 会产生竞态条件
            method2(list);
            method3(list);
        }
    }

    private void method2(List<String> list) {
        list.add("1");
    }

    private void method3(List<String> list) {
        list.remove(0);
    }

}
