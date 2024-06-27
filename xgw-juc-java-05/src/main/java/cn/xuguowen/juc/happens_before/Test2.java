package cn.xuguowen.juc.happens_before;

/**
 * ClassName: Test2
 * Package: cn.xuguowen.juc.happens_before
 * Description:happens-before规则2:volatile 变量规则 (Volatile Variable Rule)。
 *
 * @Author 徐国文
 * @Create 2024/6/27 12:05
 * @Version 1.0
 */
public class Test2 {

    // 变量x是volatile关键字修饰的。基于内存屏障技术确保了可见性。
    volatile static int x;

    public static void main(String[] args) {

        new Thread(() -> {
            x = 10;
            // 加入写屏障：写屏障保证在该屏障之前，对共享变量的改动，都同步到主存当中。
        }, "t1").start();


        new Thread(() -> {
            // 加入读屏障：读屏障保证在该屏障之后，对共享变量的读取，加载的都是主存中的最新数据
            System.out.println(x);
        }, "t2").start();
    }
}
