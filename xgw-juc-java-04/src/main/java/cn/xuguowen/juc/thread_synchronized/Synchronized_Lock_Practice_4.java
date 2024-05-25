package cn.xuguowen.juc.thread_synchronized;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Synchronized_Lock_Practice_4
 * Package: cn.xuguowen.juc.thread_synchronized
 * Description:synchronized的练习：线程八锁之四。其实就是考察 synchronized 锁住的是哪个对象
 *
 * @Author 徐国文
 * @Create 2024/5/25 10:49
 * @Version 1.0
 */
@Slf4j(topic = "c.Synchronized_Lock_Practice_4")
public class Synchronized_Lock_Practice_4 {
    public static void main(String[] args) {
        Number4 n1 = new Number4();
        Number4 n2 = new Number4();
        new Thread(n1::a).start();
        new Thread(n2::b).start();
        // 执行结果：2 1s 后 1
    }
}

@Slf4j(topic = "c.Number4")
class Number4 {
    public synchronized void a() {
        Sleeper.sleep(1);
        log.debug("1");
    }
    public synchronized void b() {
        log.debug("2");
    }
}
