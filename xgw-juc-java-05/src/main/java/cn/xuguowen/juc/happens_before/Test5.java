package cn.xuguowen.juc.happens_before;

import cn.xuguowen.juc.utils.Sleeper;

/**
 * ClassName: Test5
 * Package: cn.xuguowen.juc.happens_before
 * Description:happens-before规则5:线程中断规则 (Interruption Rule)。
 *
 * @Author 徐国文
 * @Create 2024/6/27 12:16
 * @Version 1.0
 */
public class Test5 {

    static int x;

    public static void main(String[] args) {

        // t2线程
        Thread t2 = new Thread(()->{
            while(true) {
                if(Thread.currentThread().isInterrupted()) {
                    System.out.println(x);
                    break;
                }
            }
        },"t2");
        t2.start();


        // t1线程
        new Thread(()->{
            Sleeper.sleep(1);
            // 线程 t1 打断 t2（interrupt）前对变量的写，对于其他线程得知 t2 被打断后对变量的读可见
            x = 10;
            // 打断正在sleep wait join 的线程，是会清除打断标记的。目前这个案例是不会清除打断标记的
            t2.interrupt();
        },"t1").start();


        // 主线程
        while(!t2.isInterrupted()) {
            Thread.yield();
        }
        System.out.println(x);
    }
}
