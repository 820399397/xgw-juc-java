package cn.xuguowen.juc.happens_before;

/**
 * ClassName: Test7
 * Package: cn.xuguowen.juc.happens_before
 * Description:happens-before规则7:传递性 (Transitivity)
 *
 * @Author 徐国文
 * @Create 2024/6/27 12:26
 * @Version 1.0
 */
public class Test7 {

    volatile static int x;

    static int y;

    public static void main(String[] args) {
        new Thread(()->{
            y = 10;
            // x变量是volatile关键字修饰的。
            x = 20;
            // 加入写屏障：针对于可见性，写屏障之前对共享变量的改动，都会同步到主存中
        },"t1").start();

        new Thread(()->{
            // x=20 对 t2 可见, 同时 y=10 也对 t2 可见
            System.out.println(x);
        },"t2").start();
    }
}
