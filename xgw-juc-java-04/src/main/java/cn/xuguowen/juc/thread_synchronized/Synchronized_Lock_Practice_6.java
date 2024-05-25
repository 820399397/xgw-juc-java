package cn.xuguowen.juc.thread_synchronized;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Synchronized_Lock_Practice_6
 * Package: cn.xuguowen.juc.thread_synchronized
 * Description:synchronized的练习：线程八锁之六。其实就是考察 synchronized 锁住的是哪个对象
 *
 * @Author 徐国文
 * @Create 2024/5/25 10:52
 * @Version 1.0
 */
@Slf4j(topic = "c.Synchronized_Lock_Practice_6")
public class Synchronized_Lock_Practice_6 {
    public static void main(String[] args) {
        Number6 n1 = new Number6();
        new Thread(()->{ n1.a(); }).start();
        new Thread(()->{ n1.b(); }).start();
        // 执行结果：1s 后12 或 2 1s后 1
    }
}

@Slf4j(topic = "c.Number6")
class Number6 {
    public static synchronized void a() {
        Sleeper.sleep(1);
        log.debug("1");
    }
    public static synchronized void b() {
        log.debug("2");
    }
}
