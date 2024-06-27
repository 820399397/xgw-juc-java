package cn.xuguowen.juc.balking;

/**
 * ClassName: Test1
 * Package: cn.xuguowen.juc.balking
 * Description:balking犹豫模式的习题
 * 题目需求：希望 doInit() 方法仅被调用一次，下面的实现是否有问题，为什么？
 *
 * @Author 徐国文
 * @Create 2024/6/27 14:05
 * @Version 1.0
 */
public class Test1 {

    volatile boolean initialized = false;

    static final Object lock = new Object();

    /**
     * 双重检查锁定（Double-checked locking）：
     * 1.第一次检查 initialized 标志（不加锁），如果已经初始化，则直接返回，减少不必要的同步开销。
     * 2.进入同步块后，再次检查 initialized 标志（加锁），确保在多线程环境下只有一个线程能执行 doInit() 方法。
     */
    void init() {
        if (initialized) {
            return;
        }
        synchronized (lock) {
            if (initialized) {
                return;
            }
            doInit();
            initialized = true;
        }
    }

    private void doInit() {

    }
}
