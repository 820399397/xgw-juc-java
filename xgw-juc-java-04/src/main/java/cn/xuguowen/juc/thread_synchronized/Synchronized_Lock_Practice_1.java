package cn.xuguowen.juc.thread_synchronized;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Synchronized_Practice
 * Package: cn.xuguowen.juc.thread_synchronized
 * Description:synchronized的练习：线程八锁之一。其实就是考察 synchronized 锁住的是哪个对象
 *
 *
 * @Author 徐国文
 * @Create 2024/5/25 10:37
 * @Version 1.0
 */
@Slf4j(topic = "c.Synchronized_Lock_Practice_1")
public class Synchronized_Lock_Practice_1 {
    public static void main(String[] args) {
        Number1 n1 = new Number1();
        new Thread(n1::a).start();
        new Thread(n1::b).start();
        // 执行结果： 1 2 或者 2 1
    }
}

@Slf4j(topic = "c.Number1")
class Number1 {
    public synchronized void a() {
        log.debug("1");
    }
    public synchronized void b() {
        log.debug("2");
    }
}


