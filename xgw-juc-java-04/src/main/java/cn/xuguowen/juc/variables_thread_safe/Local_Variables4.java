package cn.xuguowen.juc.variables_thread_safe;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: Local_Variables4
 * Package: cn.xuguowen.juc.thread_safe
 * Description:方法权限修饰符带来的思考：Local_Variables2 和 Local_Variables3 类中的method2()和method3()方法都是private的，意味着子类不可以重写。
 * 但是，如果method2()和method3()方法是pubLic的，那么子类可以重写。在这种情况下会不会产生线程安全问题呢？
 * 情况1：有其它线程调用 method2 和 method3。这是线程安全的。
 * 情况2：见Local_Variables5类
 *
 * @Author 徐国文
 * @Create 2024/5/26 16:21
 * @Version 1.0
 */
@Slf4j(topic = "c.Local_Variables4")
public class Local_Variables4 {

    static final int THREAD_NUMBER = 2;
    static final int LOOP_NUMBER = 200;

    public static void main(String[] args) {
        ThreadSafe5 test = new ThreadSafe5();
        for (int i = 0; i < THREAD_NUMBER; i++) {
            new Thread(() -> {
                test.method1(LOOP_NUMBER);
            }, "Thread" + i).start();
        }

        // 3.其他线程直接调用method2和method3方法.每个线程也会创建并使用自己的 ArrayList 实例，因此也不会出现线程安全问题。
        for (int i = 0; i < LOOP_NUMBER; i++) {
            new Thread(() -> {
                test.method2(new ArrayList<>());
            }, "Thread" + i).start();
        }

        for (int i = 0; i < LOOP_NUMBER; i++) {
            new Thread(() -> {
                test.method3(new ArrayList<>());
            }, "Thread" + i).start();
        }
    }
}

@Slf4j(topic = "c.Threadsafe4")
class ThreadSafe4 {

    public void method1(int loopNumber) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < loopNumber; i++) {
            // 临界区, 会产生竞态条件
            method2(list);
            method3(list);
        }
    }

    public void method2(List<String> list) {
        list.add("1");
    }

    public void method3(List<String> list) {
        list.remove(0);
    }

}
