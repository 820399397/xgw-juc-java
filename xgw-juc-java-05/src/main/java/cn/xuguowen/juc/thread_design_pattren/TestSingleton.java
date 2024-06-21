package cn.xuguowen.juc.thread_design_pattren;

/**
 * ClassName: TestSingletom
 * Package: cn.xuguowen.juc.thread_design_pattren
 * Description:balking犹豫模式还经常用来实现线程安全的单例
 *
 * @Author 徐国文
 * @Create 2024/6/21 13:19
 * @Version 1.0
 */
public final class TestSingleton {

    private TestSingleton() {
    }

    private static TestSingleton INSTANCE = null;

    public static synchronized TestSingleton getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = new TestSingleton();
        return INSTANCE;
    }
}
