package cn.xuguowen.juc.thread_safe_class;

import java.util.Hashtable;

/**
 * ClassName: Test1
 * Package: cn.xuguowen.juc.thread_safe_class
 * Description:演示：针对于常见的线程安全类：多个线程调用它们同一个实例的某个方法时，是线程安全的。
 *
 * @Author 徐国文
 * @Create 2024/5/26 16:47
 * @Version 1.0
 */
public class Test1 {

    public static void main(String[] args) {
        test2();
    }

    /**
     * 它们多个方法的组合不是原子的.
     */
    private static void test2() {
        Hashtable table = new Hashtable();
        if( table.get("key") == null) {
            table.put("key", "value");
        }
    }

    /**
     * 它们的每个方法是原子的
     */
    private static void test1() {
        Hashtable table = new Hashtable();
        new Thread(()->{
            table.put("key", "value1");
        }).start();

        new Thread(()->{
            table.put("key", "value2");
        }).start();
    }
}
