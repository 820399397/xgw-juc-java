package cn.xuguowen.juc.happens_before;

/**
 * ClassName: Test3
 * Package: cn.xuguowen.juc.happens_before
 * Description:happens-before规则3:程序顺序规则 (Program Order Rule) 和 线程启动规则 (Thread Start Rule)
 *
 * @Author 徐国文
 * @Create 2024/6/27 12:08
 * @Version 1.0
 */
public class Test3 {

    static int x;

    /**
     * 程序顺序原则：在主线程中把10赋值给x变量，线程t1一定是可以读取到这个x变量的值。
     * 线程启动原则：t1线程启动后，一定可以读取到x变量已经赋值为10。
     *
     * @param args
     */
    public static void main(String[] args) {
        x = 10;

        new Thread(() -> {
            System.out.println(x);
        }, "t1").start();
    }
}
