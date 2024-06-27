package cn.xuguowen.juc.happens_before;

/**
 * ClassName: Test
 * Package: cn.xuguowen.juc.happens_before
 * Description:happens-before规则1：监视器锁规则 (Monitor Lock Rule)，也称为 锁定规则。
 *
 * @Author 徐国文
 * @Create 2024/6/27 11:59
 * @Version 1.0
 */
public class Test1 {
    static int x = 0;

    static final Object lock = new Object();

    /**
     * synchronized会保证原子性和可见性。
     * @param args
     */
    public static void main(String[] args) {
        // t1线程一定是优先于t2线程执行的，当t1线程执行完毕之后释放锁资源后，对x的修改对于其他线程是可见的。t2线程获取锁资源读取x的值是可见的
        new Thread(() -> {
            synchronized (lock) {{
                x = 10;
            }}
        },"t1").start();

        new Thread(() -> {
            synchronized (lock) {{
                System.out.println(x);
            }}
        },"t2").start();
    }
}
