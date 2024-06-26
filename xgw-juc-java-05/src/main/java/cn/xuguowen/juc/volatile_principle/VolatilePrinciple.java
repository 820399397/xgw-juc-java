package cn.xuguowen.juc.volatile_principle;

import org.openjdk.jcstress.infra.results.I_Result;

/**
 * ClassName: VolatilePrinciple
 * Package: cn.xuguowen.juc.volatile_principle
 * Description:volatile关键字的底层原理是内存屏障，Memory Barrier（Memory Fence）。
 * - 对 volatile 变量的写指令后会加入写屏障
 * - 对 volatile 变量的读指令前会加入读屏障
 * 需要注意的是：volatile不能解决指令交错。写屏障仅仅是保证之后的读能够读到最新的结果。而有序性的保证也只是保证了本线程内相关代码不被重排序
 * @Author 徐国文
 * @Create 2024/6/26 12:02
 * @Version 1.0
 */
public class VolatilePrinciple {

    int num = 0;

    volatile boolean reday = false;

    // 线程1 执行此方法
    public void actor1(I_Result r) {
        // reaay是volatile关键字修饰的。针对于可见性，这里是读取reday的值，所以是读屏障。对 volatile 变量的读指令前会加入读屏障
        // 读屏障保证在该屏障之后，对共享变量的读取，加载的都是主存中的最新数据
        // 读屏障会确保指令重排序时，不会将读屏障之后的代码排在读屏障之前
        if(reday) {
            r.r1 = num + num;
        } else {
            r.r1 = 1;
        }
    }

    // 线程2 执行此方法
    public void actor2(I_Result r) {
        num = 2;
        reday = true;
        // 针对于可见性：这里会加入写屏障
        /*
         如何保证可见性：
         reday变量是volatile关键字修饰的，目前是一个写操作。对 volatile 变量的写指令后会加入写屏障。
         写屏障保证在该屏障之前，对共享变量的改动，都同步到主存当中。

         如何保证有序性：
         写屏障会确保指令重排序时，不会将写屏障之前的代码排在写屏障之后。这个例子中就意味着num = 2 一定不会在reday = true 之后
         */
    }
}
