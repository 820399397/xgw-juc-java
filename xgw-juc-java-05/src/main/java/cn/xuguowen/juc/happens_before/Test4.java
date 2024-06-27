package cn.xuguowen.juc.happens_before;

/**
 * ClassName: Test4
 * Package: cn.xuguowen.juc.happens_before
 * Description:happens-before规则4:线程终止规则 (Thread Termination Rule)，也称为 Thread.join() 规则。
 *
 * @Author 徐国文
 * @Create 2024/6/27 12:14
 * @Version 1.0
 */
public class Test4 {

    static int x;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            x = 10;
        }, "t1");
        t1.start();

        t1.join();
        System.out.println(x);
    }
}
