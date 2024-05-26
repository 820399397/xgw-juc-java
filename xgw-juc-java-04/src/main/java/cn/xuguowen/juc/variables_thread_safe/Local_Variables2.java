package cn.xuguowen.juc.variables_thread_safe;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * ClassName: Local_Variables2
 * Package: cn.xuguowen.juc.thread_safe
 * Description:演示局部变量引用的对象逃离方法作用范围
 *
 * @Author 徐国文
 * @Create 2024/5/26 16:04
 * @Version 1.0
 */
@Slf4j(topic = "c.Local_Variables2")
public class Local_Variables2 {
    static final int THREAD_NUMBER = 2;
    static final int LOOP_NUMBER = 200;

    public static void main(String[] args) {
        // 1.这个对象是被多个线程共享的
        ThreadUnsafe2 test = new ThreadUnsafe2();
        // 2.启动多个线程，分别调用method1方法
        for (int i = 0; i < THREAD_NUMBER; i++) {
            new Thread(() -> {
                test.method1(LOOP_NUMBER);
            }, "Thread" + i).start();
        }
    }
}

/**
 * 在 ThreadUnsafe 类中，list 是一个成员变量，而不是局部变量。
 * 然而，method2 和 method3 方法对 list 进行操作时，list 对象的引用是从 ThreadUnsafe 类传递下来的。这意味着虽然 list 不是局部变量，但它的引用逃离了方法的作用范围，被多个线程共享和操作。
 * 由于多个线程同时对共享的 list 对象进行操作而没有进行适当的同步处理，可能发生以下几种情况：
 *  - 当一个线程在遍历 list 的同时，另一个线程修改了 list，会抛出 ConcurrentModificationException 异常。
 *  - 由于线程切换的原因，list.add("1") 和 list.remove(0) 操作不是原子性的，可能会导致数据丢失。例如，一个线程执行 list.add("1") 后还没来得及增加，另一个线程就移除了一个元素，导致增加的元素被误删。
 *  - 如果 list 为空，而一个线程执行 list.remove(0)，会抛出 IndexOutOfBoundsException 异常。
 *  - 由于并发操作，list 的最终结果可能不一致。例如，预期 list 最终应该包含200个元素，但由于竞态条件，最终可能会包含更多或更少的元素。
 */
@Slf4j(topic = "c.ThreadUnsafe2")
class ThreadUnsafe2 {
    ArrayList<String> list = new ArrayList<>();

    public void method1(int loopNumber) {
        for (int i = 0; i < loopNumber; i++) {
            // 临界区, 会产生竞态条件
            method2();
            method3();
        }
    }

    private void method2() {
        list.add("1");
    }

    private void method3() {
        list.remove(0);
    }


}