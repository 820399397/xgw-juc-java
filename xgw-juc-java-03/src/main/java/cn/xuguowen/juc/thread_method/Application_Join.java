package cn.xuguowen.juc.thread_method;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Application_Join
 * Package: cn.xuguowen.juc.thread_method
 * Description:应用之同步
 * 1.区别同步和异步：以调用方角度来讲，如果需要等待结果返回，才能继续运行就是同步。不需要等待结果返回，就能继续运行就是异步。
 *
 * @Author 徐国文
 * @Create 2024/5/20 12:48
 * @Version 1.0
 */
@Slf4j(topic = "c.Application_Join")
public class Application_Join {

    static int a = 0;
    static int b = 0;

    public static void main(String[] args) throws InterruptedException {
        test2();
    }

    /**
     * 如果将test1()方法内的两个join()方法的调用顺序颠倒一下呢？
     * cost: 2029
     */
    private static void test2() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            Sleeper.sleep(1);
            a = 10;
        },"t1");

        Thread t2 = new Thread(() -> {
            Sleeper.sleep(2);
            b = 20;
        },"t2");

        // 主线程记录开始和结束时间
        long start = System.currentTimeMillis();
        t1.start();
        t2.start();
        // 第一个 join：等待 t2 时, t1 并没有停止, 而在运行，t1而且都已经运行完毕了
        t2.join();
        // 第二个 join：2s 后, 执行到此, t1 运行结束了, 因此无需等待
        t1.join();
        long end = System.currentTimeMillis();
        log.debug("a: {} b: {} cost: {}", a, b, end - start);
    }

    /**
     * 如下代码花费的时间是多长呢？
     * cost: 2029
     */
    private static void test1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            Sleeper.sleep(1);
            a = 10;
        },"t1");

        Thread t2 = new Thread(() -> {
            Sleeper.sleep(2);
            b = 20;
        },"t2");

        // 主线程记录开始和结束时间
        long start = System.currentTimeMillis();
        t1.start();
        t2.start();
        // 第一个 join：等待 t1 时, t2 并没有停止, 而在运行
        t1.join();
        // 第二个 join：1s 后, 执行到此, t2 也运行了 1s, 因此也只需再等待 1s
        t2.join();
        long end = System.currentTimeMillis();
        log.debug("a: {} b: {} cost: {}", a, b, end - start);
    }
}
