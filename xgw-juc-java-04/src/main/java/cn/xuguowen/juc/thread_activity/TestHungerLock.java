package cn.xuguowen.juc.thread_activity;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: TestHungerLock
 * Package: cn.xuguowen.juc.thread_activity
 * Description:饥饿:满一航老师教程中饥饿是这样描述的：很多教程中把饥饿定义为，一个线程由于优先级太低，始终得不到 CPU 调度执行，也不能够结束，饥饿的情况不易演示，讲读写锁时会涉及饥饿问题
 * 案例场景：哲学家就餐问题。当使用顺序加锁解决了死锁问题之后，就出出现饥饿问题。
 *
 *
 * @Author 徐国文
 * @Create 2024/6/11 21:17
 * @Version 1.0
 */
public class TestHungerLock {
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

        // 控制台输出的大概信息，会发现 哲学家 阿基米德 大概率拿不到筷子，吃不到饭。所以产生了饥饿
        // 21:27:58 [亚里士多德] c.Philosopher - eating...
        // 21:27:59 [亚里士多德] c.Philosopher - eating...
        // 21:28:00 [亚里士多德] c.Philosopher - eating...
        // 21:28:01 [赫拉克利特] c.Philosopher - eating...
        // 21:28:01 [柏拉图] c.Philosopher - eating...
        // 21:28:02 [赫拉克利特] c.Philosopher - eating...
        // 21:28:02 [柏拉图] c.Philosopher - eating...
        // 21:28:03 [赫拉克利特] c.Philosopher - eating...
        // 21:28:03 [柏拉图] c.Philosopher - eating...
        // 21:28:04 [赫拉克利特] c.Philosopher - eating...
        // 21:28:05 [赫拉克利特] c.Philosopher - eating...
        // 21:28:06 [赫拉克利特] c.Philosopher - eating...
        // 21:28:07 [赫拉克利特] c.Philosopher - eating...
        // 21:28:08 [亚里士多德] c.Philosopher - eating...
        // 21:28:09 [赫拉克利特] c.Philosopher - eating...
        // 21:28:10 [赫拉克利特] c.Philosopher - eating...
        // 21:28:11 [赫拉克利特] c.Philosopher - eating...
        // 21:28:12 [赫拉克利特] c.Philosopher - eating...
    }
}

// 筷子类
class Chopsticks1 {
    private String name;

    public Chopsticks1(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "筷子{" + name + '}';
    }
}

@Slf4j(topic = "c.Philosopher1")
class Philosopher1 extends Thread {
    private Chopsticks left;
    private Chopsticks right;

    public Philosopher1(String name, Chopsticks left, Chopsticks right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    private void eat() {
        log.debug("eating...");
        Sleeper.sleep(0.5);
    }

    @Override
    public void run() {
        while (true) {
            // 获得左手筷子
            synchronized (left) {
                // 获得右手筷子
                synchronized (right) {
                    // 吃饭
                    this.eat();
                }
                // 放下右手筷子
            }
            // 放下左手筷子
        }
    }
}
