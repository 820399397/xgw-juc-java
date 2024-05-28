package cn.xuguowen.juc.thread_exercises;

import java.util.concurrent.ThreadLocalRandom;

/**
 * ClassName: ExerciseTransfer
 * Package: cn.xuguowen.juc.thread_exercises
 * Description:线程安全问题的练习-转账
 * 分析：线程1 和 线程2共享的资源有 账户a 和 账户b。其每个对象内部的money成员变量也是被多线程共享的，而且还存在读写操作。
 * 解决方案：使用synchronized关键字对共享资源的操作进行加锁，锁对象是账户类的Class对象。效率很低，如果此时存在 账户c 和 账户d 直接的转账操作，就无法进行了。
 * 疑问；为何锁对象不是this呢？
 *      需要注意当前案例中存在两个实例对象被共享，而不同于之前见到的多线程共享同一个实例。
 *      如果在transfer方法中对this和target两个账户加锁，可能会引入死锁风险。假设线程A试图从账户A转账到账户B，同时线程B试图从账户B转账到账户A，这时如果两个线程都持有对方的锁，就会发生死锁。
 * @Author 徐国文
 * @Create 2024/5/28 13:13
 * @Version 1.0
 */
public class ExerciseTransfer {

    public static int randomMoney() {
        return ThreadLocalRandom.current().nextInt(100) + 1;
    }

    public static void main(String[] args) throws InterruptedException {
        Account a = new Account(1000);
        Account b = new Account(1000);

        // 线程1 模拟 a 给 b 转账
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                a.transfer(b, randomMoney());
            }
        },"t1");

        // 线程2 模拟 b 给 a 转账
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                b.transfer(a, randomMoney());
            }
        },"t2");

        // 启动线程
        t1.start();
        t2.start();

        // 等待线程执行完毕，统计总金额
        t1.join();
        t2.join();

        System.out.println("总金额：" + (a.getMoney() + b.getMoney()));
    }
}

class Account {
    private int money;

    public Account(int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * 转账方法
     *
     * @param target    目标对象
     * @param money     转账金额
     */
    public void transfer(Account target, int money) {
        synchronized (Account.class) {
            if (this.money >= money) {
                this.setMoney(this.money - money);
                target.setMoney(target.getMoney() + money);
            }
        }
    }
}
