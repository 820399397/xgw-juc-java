package cn.xuguowen.juc.thread_pool;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * ClassName: TestNewCachedThreadPool
 * Package: cn.xuguowen.juc.thread_pool
 * Description:测试NewCachedThreadPool（可缓存的线程池）的使用：线程池会根据需要创建新的救急线程，如果有空闲的救急线程可用，则会重用空闲的救急线程。线程池中的救急线程如果在60秒内没有被使用，将会被终止并移出线程池。
 * 方法源码如下：核心线程数是 0， 最大线程数是 Integer.MAX_VALUE，救急线程的空闲生存时间是 60s，意味着全部都是救急线程（60s 后可以回收），救急线程可以无限创建
 * public static ExecutorService newCachedThreadPool() {
 * return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
 * 60L, TimeUnit.SECONDS,
 * new SynchronousQueue<Runnable>());
 * }
 * <p>
 * 线程池的特点：
 * - 线程动态增长：线程池会根据需要创建新线程来执行任务，理论上线程数量没有上限。
 * - 线程重用：如果线程池中有空闲线程可用，会重用这些线程来执行新的任务。
 * - 线程回收：线程在空闲60秒后会被回收和终止，避免占用系统资源。
 * <p>
 * 注意事项：因为newCachedThreadPool会根据需要创建新线程，因此在任务提交速率高于任务处理速率时，可能会创建大量线程，导致系统资源耗尽。
 * <p>
 * SynchronousQueue队列的介绍：没有任何内部容量，甚至连一个元素的缓存空间都没有。每一个插入操作必须等待另一个线程的对应删除操作，反之亦然。它非常适合于需要高效传递任务的场景，例如在生产者-消费者模式中。
 * - 零容量：没有容量，无法存储元素。每一个 put 操作必须等待一个 take 操作，反之亦然。
 * - 直接传递：元素在生产者和消费者之间直接传递，不进行缓冲。每次插入操作必须等到另一个线程来取出元素，否则插入操作将被阻塞。
 *
 * 使用场景：整个线程池表现为线程数会根据任务量不断增长，没有上限，当任务执行完毕，空闲 1分钟后释放线程。 适合任务数比较密集，但每个任务执行时间较短的情况
 *
 *
 * @Author 徐国文
 * @Create 2024/7/17 13:01
 * @Version 1.0
 */
@Slf4j(topic = "c.TestNewCachedThreadPool")
public class TestNewCachedThreadPool {
    public static void main(String[] args) {
        test1();

        // test5();

    }

    /**
     * SynchronousQueue队列的测试
     */
    private static void test5() {
        SynchronousQueue<Integer> integers = new SynchronousQueue<>();
        new Thread(() -> {
            try {
                log.debug("putting {} ", 1);
                integers.put(1);
                log.debug("{} putted...", 1);

                log.debug("putting...{} ", 2);
                integers.put(2);
                log.debug("{} putted...", 2);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        Sleeper.sleep(1);

        new Thread(() -> {
            try {
                log.debug("taking {}", 1);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();

        Sleeper.sleep(1);

        new Thread(() -> {
            try {
                log.debug("taking {}", 2);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t3").start();

    }


    private static void test1() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 1.可以测试等待60s过后，程序自动就结束了。说明救急线程60s过后就自动回收了
        executorService.execute(() -> {
            // 抛出异常，下面的代码不会执行了
            int i = 1 / 0;
            log.debug("1");
        });

        executorService.execute(() -> {
            log.debug("2");
        });

        executorService.execute(() -> {
            log.debug("3");
        });
    }
}

