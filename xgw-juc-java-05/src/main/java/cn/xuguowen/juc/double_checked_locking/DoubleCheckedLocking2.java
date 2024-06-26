package cn.xuguowen.juc.double_checked_locking;

/**
 * ClassName: DoubleCheckedLockingTest1
 * Package: cn.xuguowen.juc.double_checked_locking
 * Description:1.对DoubleCheckedLocking1的效率优化
 *
 * @Author 徐国文
 * @Create 2024/6/26 13:39
 * @Version 1.0
 */
public final class DoubleCheckedLocking2 {

    private DoubleCheckedLocking2() {
    }

    public static DoubleCheckedLocking2 INSTANCE = null;

    /**
     * 双重检查锁定优化：首次使用 getInstance() 才使用 synchronized 加锁，后续使用时无需加锁
     * 有隐含的，但很关键的一点：第一个 if 使用了 INSTANCE 变量，是在同步块之外
     * 这里先记住一个知识点：synchronized关键字可以保证原子性、有序性、可见性。有序性得以保证的前提是这个共享变量完全被synchronized关键字保护起来，
     * 在多线程环境下，这个代码是有问题的。
     * @return
     */
    public static DoubleCheckedLocking2 getInstance() {
        if (INSTANCE == null) {
            synchronized (DoubleCheckedLocking2.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DoubleCheckedLocking2();
                }
            }
        }
        return INSTANCE;
    }
}
