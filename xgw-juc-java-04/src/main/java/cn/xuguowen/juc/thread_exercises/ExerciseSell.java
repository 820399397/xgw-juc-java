package cn.xuguowen.juc.thread_exercises;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ClassName: ExerciseSell
 * Package: cn.xuguowen.juc.thread_exercises
 * Description:线程安全问题的练习-售票
 * 由于不好测试出结果的原因，写了如下的脚本，方便测试：
 * 一定要在target/classes目录下执行：
 * for ($n=1; $n -le 10; $n++) {
 * java cn.xuguowen.juc.thread_exercises.ExerciseSell
 * }
 * <p>
 * 测试发现这段代码是存在线程安全问题的，如何解决？
 * 1.只需要给售票的方法加锁即可
 * 2.或许你会有疑问：为何不在线程内部，对线程内的代码全部加锁。如下：
 * Thread clientThread = new Thread(() -> {
 *      synchronized (window) {
 *          // 抢票
 *          int amount = window.sell(randomAmount());
 *          // 记录卖去出票的数量
 *          amountList.add(amount);
 *      }
 * });
 * 不这样解决的原因是：amountList是线程安全的List对象。其次这种加锁的方式适用于同一个实例对象的不同方法。
 * 比如Hashtable，你单独调用某个方法，它是线程安全的。但是调用多个方法，就不是线程安全的了。
 *
 * 其次:如何分析一段代码中是否存在线程安全问题？
 * 你只需要把握临界区的概念即可：一段代码块内如果存在对共享资源的多线程读写操作，称这段代码块为临界区
 * @Author 徐国文
 * @Create 2024/5/28 12:27
 * @Version 1.0
 */
public class ExerciseSell {

    // Random 类的 next 方法在生成随机数时使用了 CAS 操作来确保某些原子操作的安全性。
    // 但是，这并不意味着 Random 类是完全线程安全的。在高并发环境下，多个线程同时访问 Random 实例可能会导致性能下降和随机数生成质量的问题。
    // java.util.concurrent.ThreadLocalRandom：Java 7 引入了 ThreadLocalRandom 类，它为每个线程提供一个独立的 Random 实例，从而避免了线程之间的竞争。这是推荐在多线程环境中使用的方式。
    static Random random = new Random();

    // 随机生成购买的票数 [1~5]
    public static int randomAmount() {
        return ThreadLocalRandom.current().nextInt(5) + 1;
    }

    public static void main(String[] args) {
        // 模拟售票窗口：当前只有一个窗口售票，余票数只有 1000 张
        TicketWindow window = new TicketWindow(2000);

        // 客户端线程列表：不用线程安全的list类，是因为这个对象不会被多个线程共享，只是在主线程中使用
        List<Thread> clientThreadList = new ArrayList<>();

        // 存储购买票数的总和:要使用线程安全的list类。因为被多个线程共享，会引发线程安全问题的。
        List<Integer> amountList = new Vector<>();

        // 模拟 2000 个客户端同时抢票
        for (int i = 0; i < 2000; i++) {
            Thread clientThread = new Thread(() -> {
                // 抢票
                int amount = window.sell(randomAmount());
                // 记录卖去出票的数量
                amountList.add(amount);
            });

            // 把每个线程对象添加到列表中
            clientThreadList.add(clientThread);

            // 启动客户端抢票线程
            clientThread.start();
        }

        // 主线程必须得等到所有线程执行完毕之后，再去获取余票数量
        clientThreadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // 验证程序是否正确：就看最后卖出去的票和余票的个数相加，是否等于最初的余票数量
        System.out.println("余票数量：" + window.getCount());
        System.out.println("卖出去票的总和：" + amountList.stream().mapToInt(Integer::intValue).sum());

    }
}


class TicketWindow {
    private int count;

    public TicketWindow(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    /**
     * 售票方法
     *
     * @param amount 购买票的数量
     * @return
     */
    public synchronized int sell(int amount) {
        if (count >= amount) {
            count = count - amount;
            return amount;
        } else {
            return 0;
        }
    }
}


