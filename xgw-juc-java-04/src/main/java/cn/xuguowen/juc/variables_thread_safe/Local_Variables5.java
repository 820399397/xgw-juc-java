package cn.xuguowen.juc.variables_thread_safe;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: Local_Variables5
 * Package: cn.xuguowen.juc.thread_safe
 * Description:补充：对Local_Variables4类的情况进行补充
 * 情况2：为 ThreadSafe 类添加子类，子类覆盖 method2 或 method3 方法，
 *
 * 从这个例子可以看出 private 或 final 提供【安全==>线程安全】的意义所在，请体会开闭原则中的【闭】
 *  - 线程安全：将method1() 和 method2() 和 method() 方法都声明为final的 或者 method2() 和 method() 方法都声明为private的.
 *  - 开闭原则：对修改关闭，使得类的行为在扩展时不会受到影响，保持代码的稳定性和可维护性。
 * @Author 徐国文
 * @Create 2024/5/26 16:34
 * @Version 1.0
 */
@Slf4j(topic = "c.Local_Variables5")
public class Local_Variables5 {
    static final int THREAD_NUMBER = 2;

    static final int LOOP_NUMBER = 200;

    public static void main(String[] args) {
        ThreadSafe5SubClass test = new ThreadSafe5SubClass();
        for (int i = 0; i < THREAD_NUMBER; i++) {
            new Thread(() -> {
                test.method1(LOOP_NUMBER);
            }, "Thread" + i).start();
        }

    }
}

@Slf4j(topic = "c.ThreadSafe5")
class ThreadSafe5 {
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

/**
 * 1.在 ThreadSafe5 类中的 method1 方法里，创建了一个 ArrayList 实例 list，并在循环中多次调用 method2 和 method3。在这个过程中，list 是线程私有的。
 * 2.主线程在循环中调用 method2 和 method3，而 method3 又启动新线程并发执行 list.remove(0)。
 * 3.这种情况下，list 的修改操作（add 和 remove）是并发的，ArrayList 不是线程安全的，会导致数据不一致或异常。
 */
@Slf4j(topic = "c.ThreadSafe5SubClass")
class ThreadSafe5SubClass extends ThreadSafe5 {

    @Override
    public void method3(List<String> list) {
        new Thread(() -> {
            list.remove(0);
        }).start();
    }
}
