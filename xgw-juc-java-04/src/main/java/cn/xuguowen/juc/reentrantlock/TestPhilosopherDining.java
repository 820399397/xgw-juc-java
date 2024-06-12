package cn.xuguowen.juc.reentrantlock;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: TestPhilosopherDining
 * Package: cn.xuguowen.juc.reentrantlock
 * Description:哲学家就餐问题：使用ReentrantLock解决死锁的发生
 *
 * @Author 徐国文
 * @Create 2024/6/12 21:11
 * @Version 1.0
 */
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
        new Philosopher("阿基米德", c5, c1).start();

        // 避免死锁的策略：顺序加锁
        // new Philosopher("阿基米德", c1, c5).start();
    }
}

// 筷子类
class Chopsticks extends ReentrantLock {
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
            // 获得左手筷子 锁对象就是筷子，所以筷子类实现ReentrantLock类
            if (left.tryLock()) {
                try {
                    if (right.tryLock()) {       // 获得右手筷子
                        try {
                            this.eat();         // 筷子都获取到了。开始吃饭
                        } finally {
                            right.unlock();     // 放弃右筷子
                        }
                    }
                } finally {
                    left.unlock();              // 放弃左筷子
                }
            }
        }
    }
}

