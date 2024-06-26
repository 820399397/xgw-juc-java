package cn.xuguowen.juc.double_checked_locking;

/**
 * ClassName: DoubleCheckedLockingTest1
 * Package: cn.xuguowen.juc.double_checked_locking
 * Description:1.实现懒汉式的单例设计模式
 *
 * @Author 徐国文
 * @Create 2024/6/26 13:39
 * @Version 1.0
 */
public final class DoubleCheckedLocking1 {

    private DoubleCheckedLocking1() {
    }

    public static DoubleCheckedLocking1 INSTANCE = null;

    /**
     * 这个代码存在效率上的问题
     * 因为每次调用getInstance方法时都会进行同步，即使实例已经创建因此，需要进行进一步的优化
     * @return
     */
    public static DoubleCheckedLocking1 getInstance() {
        synchronized (DoubleCheckedLocking1.class) {
            if (INSTANCE == null) {
                INSTANCE = new DoubleCheckedLocking1();
            }
        }
        return INSTANCE;
    }
}
