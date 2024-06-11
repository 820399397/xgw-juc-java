package cn.xuguowen.juc.thread_activity;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: TestPhilosopherDining
 * Package: cn.xuguowen.juc.thread_activity
 * Description:哲学家就餐问题，演示死锁问题。
 *
 * @Author 徐国文
 * @Create 2024/6/11 20:42
 * @Version 1.0
 */
@Slf4j(topic = "c.TestPhilosopherDining")
public class TestPhilosopherDining {
    public static void main(String[] args) {
        // 就餐
        Chopsticks c1 = new Chopsticks("1");
        Chopsticks c2 = new Chopsticks("2");
        Chopsticks c3 = new Chopsticks("3");
        Chopsticks c4 = new Chopsticks("4");
        Chopsticks c5 = new Chopsticks("5");
        // 发生死锁问题
        new Philosopher("苏格拉底", c1, c2).start();
        new Philosopher("柏拉图", c2, c3).start();
        new Philosopher("亚里士多德", c3, c4).start();
        new Philosopher("赫拉克利特", c4, c5).start();
        // new Philosopher("阿基米德", c5, c1).start();

        // 避免死锁的策略：顺序加锁
        new Philosopher("阿基米德", c1, c5).start();
    }
}

// 筷子类
class Chopsticks {
    private String name;

    public Chopsticks(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "筷子{" + name + '}';
    }
}

@Slf4j(topic = "c.Philosopher")
class Philosopher extends Thread {
    private Chopsticks left;
    private Chopsticks right;

    public Philosopher(String name, Chopsticks left, Chopsticks right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    private void eat() {
        log.debug("eating...");
        Sleeper.sleep(1);
    }

    @Override
    public void run() {
        while (true) {
            // 获得左手筷子
            synchronized (left) {
                // 获得右手筷子
                synchronized (right){
                    // 吃饭
                    this.eat();
                }
                // 放下右手筷子
            }
            // 放下左手筷子
        }
    }
}

