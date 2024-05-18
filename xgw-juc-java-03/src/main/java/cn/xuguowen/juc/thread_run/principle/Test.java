package cn.xuguowen.juc.thread_run.principle;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Test
 * Package: cn.xuguowen.juc.thread_run.principle
 * Description:通过debug案例代码查看线程运行的原理
 *
 * @Author 徐国文
 * @Create 2024/5/18 20:07
 * @Version 1.0
 */
@Slf4j(topic = "c.Test")
public class Test {
    public static void main(String[] args) {
        new Thread(() -> method1(20),"t1").start();

        method1(10);
    }

    private static Object method1(int x) {
        int y = x + 1;
        Object m = method2();
        return m;
    }

    private static Object method2() {
        return new Object();
    }
}
