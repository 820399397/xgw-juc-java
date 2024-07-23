package cn.xuguowen.juc.pattern;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ClassName: TestDivideWorkPattern
 * Package: cn.xuguowen.juc.pattern
 * Description:异步模式之工作线程：让有限的工作线程（Worker Thread）来轮流异步处理无限多的任务。也可以将其归类为分工模式，它的典型实现就是线程池，也体现了经典设计模式中的享元模式。
 * 举一个生活中的例子：海底捞的服务员（线程），轮流处理每位客人的点餐（任务），如果为每位客人都配一名专属的服务员，那么成本就太高了（对比另一种多线程设计模式：Thread-Per-Message）
 * 注意：不同任务类型应该使用不同的线程池，这样能够避免饥饿，并能提升效率。
 * 例如，如果一个餐馆的工人既要招呼客人（任务类型A），又要到后厨做菜（任务类型B）显然效率不咋地，分成服务员（线程池A）与厨师（线程池B）更为合理。
 *
 * 这个案例就引发了一个思考：创建多少线程池合适呢？
 *  - 过小会导致程序不能充分地利用系统资源、容易导致饥饿
 *  - 过大会导致更多的线程上下文切换，占用更多内存
 *
 * 那如何分配线程池呢？
 * - CPU 密集型运算：通常采用 cpu 核数 + 1 能够实现最优的 CPU 利用率，+1 是保证当线程由于页缺失故障（操作系统）或其它原因导致暂停时，额外的这个线程就能顶上去，保证 CPU 时钟周期不被浪费
 * - I/O 密集型运算：CPU 不总是处于繁忙状态，例如，当你执行业务计算时，这时候会使用 CPU 资源，但当你执行 I/O 操作时、远程RPC 调用时，包括进行数据库操作时，这时候 CPU 就闲下来了，你可以利用多线程提高它的利用率。
 *  经验公式如下：线程数 = 核数 * 期望 CPU 利用率 * 总时间(CPU计算时间+等待时间) / CPU 计算时间
 *  例如 4 核 CPU 计算时间是 50% ，其它等待时间是 50%，期望 cpu 被 100% 利用，套用公式：4 * 100% * 100% / 50% = 8
 *  例如 4 核 CPU 计算时间是 10% ，其它等待时间是 90%，期望 cpu 被 100% 利用，套用公式：4 * 100% * 100% / 10% = 40
 *
 *
 * @Author 徐国文
 * @Create 2024/7/23 9:17
 * @Version 1.0
 */
@Slf4j(topic = "c.TestDivideWorkPattern")
public class TestDivideWorkPattern {

    // 菜谱列表
    static final List<String> MENU = Arrays.asList("地三鲜", "宫保鸡丁", "辣子鸡丁", "烤鸡翅");

    // 随机做一道菜
    static Random RANDOM = new Random();
    static String cooking() {
        return MENU.get(RANDOM.nextInt(MENU.size()));
    }


    public static void main(String[] args) {
        // testNormal();

        // testHunger();

        testDivideWorkThread();
    }

    /**
     * 固定大小的线程池出现了饥饿的现象，现在我们要解决。、
     * 解决方法可以增加线程池的大小，不过不是根本解决方案，还是前面提到的，不同的任务类型，采用不同的线程。
     */
    private static void testDivideWorkThread() {
        ExecutorService waiterPool = Executors.newFixedThreadPool(1);
        ExecutorService cookPool = Executors.newFixedThreadPool(1);

        waiterPool.execute(() -> {
            log.debug("处理点餐...");
            Future<String> f = cookPool.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜: {}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        waiterPool.execute(() -> {
            log.debug("处理点餐...");
            Future<String> f = cookPool.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜: {}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * 固定大小线程池会有饥饿现象,在testHunger()方法去测试了
     * 两个工人是同一个线程池中的两个线程。他们要做的事情是：为客人点餐和到后厨做菜，这是两个阶段的工作。
     * 客人点餐：必须先点完餐，等菜做好，上菜，在此期间处理点餐的工人必须等待
     * 后厨做菜：没啥说的，做就是了
     * 比如工人A 处理了点餐任务，接下来它要等着 工人B 把菜做好，然后上菜，他俩也配合的蛮好
     */
    private static void testNormal() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(() -> {
            log.debug("处理点餐...");
            Future<String> f = executorService.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜: {}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }


    /**
     * 固定大小线程池会有饥饿现象
     * 测试饥饿:现在同时来了两个客人，这个时候工人A 和工人B 都去处理点餐了，这时没人做饭了，饥饿
     */
    public static void testHunger() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(() -> {
            log.debug("处理点餐...");
            Future<String> f = executorService.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜: {}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        executorService.execute(() -> {
            log.debug("处理点餐...");
            Future<String> f = executorService.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜: {}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
