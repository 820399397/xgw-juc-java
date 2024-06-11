package cn.xuguowen.juc.thread_activity;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

/**
 * ClassName: TestLiveLock2
 * Package: cn.xuguowen.juc.thread_activity
 * Description:活锁案例2:在这个例子中，两个线程（丈夫和妻子）不断地试图让对方先吃，结果导致谁也吃不到饭。这就是活锁的典型表现：线程虽然不断地在运行，但由于相互礼让，系统无法取得进展。
 * 1.解决活锁的办法：
 *  - 随机退避：线程在每次尝试失败后，随机等待一段时间，再进行下一次尝试。
 *  - 优先级设置：给每个线程设定一个优先级，保证优先级高的线程先进行操作。
 *  - 固定尝试次数：限制每个线程的尝试次数，超过次数后放弃当前操作或者切换到其他操作。
 * @Author 徐国文
 * @Create 2024/6/11 21:01
 * @Version 1.0
 */
@Slf4j(topic = "c.TestLiveLock2")
public class TestLiveLock2 {
    public static void main(String[] args) {
        // 丈夫
        final Diner husband = new Diner("Husband");
        // 妻子
        final Diner wife = new Diner("Wife");
        // 勺子：所有者是丈夫
        final Spoon spoon = new Spoon(husband);

        // 启动两个线程，分别让丈夫和妻子尝试用同一把勺子吃饭。
        new Thread(() -> husband.eatWith(spoon, wife)).start();
        new Thread(() -> wife.eatWith(spoon, husband)).start();
    }

}

/**
 * Spoon 类表示一个可以用来吃饭的勺子，勺子有一个当前持有者（owner）。
 */
@Slf4j(topic = "c.Spoon")
class Spoon {
    private Diner owner;

    public Spoon(Diner owner) {
        this.owner = owner;
    }

    public synchronized Diner getOwner() {
        return owner;
    }

    public synchronized void setOwner(Diner owner) {
        this.owner = owner;
    }

    public synchronized void use() {
        log.debug("{} has eaten!", owner.getName());
    }
}

/**
 * Diner 类表示一个正在尝试吃饭的人。
 */
@Slf4j(topic = "c.Diner")
class Diner {
    private String name;
    private boolean isHungry;


    public Diner(String name) {
        this.name = name;
        this.isHungry = true;
    }

    public String getName() {
        return name;
    }

    public boolean isHungry() {
        return isHungry;
    }

    /**
     * 表示用勺子吃饭的过程。如果勺子不在自己手里，就稍作等待。如果配偶也饿着，就让配偶先吃。
     *
     * @param spoon
     * @param spouse
     */
    public void eatWith(Spoon spoon, Diner spouse) {
        while (isHungry) {
            // 不断的尝试
            if (spoon.getOwner() != this) {
                try {
                    Thread.sleep(1); // 稍作等待
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            if (spouse.isHungry()) {
                log.debug("{}: You eat first my darling {}!\n", name, spouse.getName());
                spoon.setOwner(spouse);

                // 引入随机退避机制，指令交错执行就好
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(10)); // 随机等待时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }

            spoon.use();
            isHungry = false;
            log.debug("{}: I am no longer hungry.\n", name);
            spoon.setOwner(spouse);
        }
    }
}
