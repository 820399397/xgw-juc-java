package cn.xuguowen.juc.thread_synchronized;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Synchronized_Lock_Practice_3
 * Package: cn.xuguowen.juc.thread_synchronized
 * Description:synchronized的练习：线程八锁之三。其实就是考察 synchronized 锁住的是哪个对象
 *
 * @Author 徐国文
 * @Create 2024/5/25 10:42
 * @Version 1.0
 */
@Slf4j(topic = "c.Synchronized_Lock_Practice_3")
public class Synchronized_Lock_Practice_3 {
    public static void main(String[] args) {
        Number3 n3 = new Number3();
        new Thread(n3::a).start();
        new Thread(n3::b).start();
        new Thread(n3::c).start();
        // 执行结果 3 1s 1 2 或 3 2 1s 1 或 2 3 1s 1
    }
}

@Slf4j(topic = "c.Number3")
class Number3 {
    public synchronized void a() {
        Sleeper.sleep(1);
        log.debug("1");
    }
    public synchronized void b() {
        log.debug("2");
    }
    public void c() {
        log.debug("3");
    }

}