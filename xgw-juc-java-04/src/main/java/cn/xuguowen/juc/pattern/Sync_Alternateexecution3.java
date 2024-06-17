package cn.xuguowen.juc.pattern;

import cn.xuguowen.juc.utils.Sleeper;

import java.util.concurrent.locks.LockSupport;

/**
 * ClassName: Sync_Alternateexecution3
 * Package: cn.xuguowen.juc.pattern
 * Description:同步模式之交替执行。
 * 题目要求：线程 1 输出 a 5 次，线程 2 输出 b 5 次，线程 3 输出 c 5 次。现在要求输出 abcabcabcabcabc 怎么实现。
 * 当前代码是使用LockSupport的park和unpark实现的。
 *
 * @Author 徐国文
 * @Create 2024/6/17 12:17
 * @Version 1.0
 */
public class Sync_Alternateexecution3 {

    static Thread t1;

    static Thread t2;

    static Thread t3;

    public static void main(String[] args) {
        ParkUnPark park = new ParkUnPark(5);

        t1 = new Thread(() -> {
            park.print("a",t2);
        });

        t2 = new Thread(() -> {
            park.print("b",t3);
        });

        t3 = new Thread(() -> {
            park.print("c",t1);
        });

        t1.start();
        t2.start();
        t3.start();

        // 程序的运行是主线程发起
        Sleeper.sleep(1);
        System.out.println("开始...");
        LockSupport.unpark(t1);
    }
}

class ParkUnPark {
    private Integer loopNumber;

    public ParkUnPark(Integer loopNumber) {
        this.loopNumber = loopNumber;
    }

    public void print(String content,Thread next) {
        for (int i = 0; i < loopNumber; i++) {
            LockSupport.park();
            System.out.print(content);
            LockSupport.unpark(next);
        }
    }
}