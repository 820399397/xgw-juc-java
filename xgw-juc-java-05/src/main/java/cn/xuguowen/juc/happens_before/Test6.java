package cn.xuguowen.juc.happens_before;

/**
 * ClassName: Test6
 * Package: cn.xuguowen.juc.happens_before
 * Description:happens-before规则6:初始化安全性规则 (Initialization Safety Rule):Java 内存模型确保一个线程在读取一个对象的字段时，总是可以看到对象构造函数对这些字段的默认初始化。
 *
 * @Author 徐国文
 * @Create 2024/6/27 12:23
 * @Version 1.0
 */
public class Test6 {

    static class MyObject {
        int value;
    }

    static MyObject obj;

    public static void main(String[] args) {
        Thread writerThread = new Thread(() -> {
            obj = new MyObject();
        });

        Thread readerThread = new Thread(() -> {
            while (obj == null) {
                // busy-waiting until obj is initialized
            }
            System.out.println(obj.value); // Should print 0
        });

        writerThread.start();
        readerThread.start();
    }
}
