package cn.xuguowen.juc.pattern;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: Sync_SequentialControl2
 * Package: cn.xuguowen.juc.pattern
 * Description:同步模式之顺序控制
 * 题目需求：有三个线程T1,T2,T3如何保证顺序执行？
 * 想要让三个线程依次执行，并且严格按照T1->T2->T3的顺序执行的话，主要就是想办法让三个线程之间可以通信，或者可以排队。
 * 想要让多个线程之间可以通信，可以通过join方法实现，还可以通过CountDownLatch、CyclicBarrier、Semaphore实现通信。
 * 想要让线程之间排队的话，可以通过线程池或者CompletableFuture实现。
 * 还可以使用wait/notify机制、park/unpark机制、ReentrantLock来实现
 *
 * @Author 徐国文
 * @Create 2024/6/17 14:20
 * @Version 1.0
 */
@Slf4j(topic = "c.Sync_SequentialControl2")
public class Sync_SequentialControl2 {

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException, ExecutionException {
        // testJoin();

        // testCountDownLatch();

        // testCyclicBarrier();

        // testSemaphore();

        // testThreadPool();

        // testCompletableFuture();

        // testCompletableFutureApprove();

        // testWaitNotify();

        // testParkUnpark();

        testReentrantLock();
    }

    static ReentrantLock lock = new ReentrantLock();
    static Condition t1Condition = lock.newCondition();

    static Condition t2Condition = lock.newCondition();

    static Condition t3Condition = lock.newCondition();


    private static void testReentrantLock() {
        Thread t1 = new Thread(() -> {
            lock.lock();
            try {
                while (!t1Runnable) {
                    try {
                        t1Condition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("{} is runnable",Thread.currentThread().getName());
                t2Runnable = true;
                t2Condition.signal();
            } finally {
                lock.unlock();
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            lock.lock();
            try {
                while (!t2Runnable) {
                    try {
                        t2Condition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("{} is runnable",Thread.currentThread().getName());
                t3Runnable = true;
                t3Condition.signal();
            } finally {
                lock.unlock();
            }
        }, "t2");

        Thread t3 = new Thread(() -> {
            lock.lock();
            try {
                while (!t3Runnable) {
                    try {
                        t3Condition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("{} is runnable",Thread.currentThread().getName());
            } finally {
                lock.unlock();
            }
        }, "t3");

        t1.start();
        t2.start();
        t3.start();

        // 主线程作为程序的发起者
        Sleeper.sleep(1);
        log.debug("开始...");

        lock.lock();
        try {
            t1Runnable = true;
            t1Condition.signal();
        } finally {
            lock.unlock();
        }
    }

    static Thread t1;
    static Thread t2;
    static Thread t3;

    /**
     * 使用park/unpark实现规定运行顺序
     */
    private static void testParkUnpark() {
        t1 = new Thread(() -> {
            LockSupport.park();
            log.debug("{} is runnable",Thread.currentThread().getName());
            LockSupport.unpark(t2);
        }, "t1");

        t2 = new Thread(() -> {
            LockSupport.park();
            log.debug("{} is runnable",Thread.currentThread().getName());
            LockSupport.unpark(t3);
        }, "t2");

        t3 = new Thread(() -> {
            LockSupport.park();
            log.debug("{} is runnable",Thread.currentThread().getName());
        }, "t3");

        t1.start();
        t2.start();
        t3.start();

        // 主线程作为程序的发起者
        Sleeper.sleep(1);
        log.debug("开始...");
        LockSupport.unpark(t1);
    }

    private static boolean t1Runnable = false;

    private static boolean t2Runnable = false;

    private static boolean t3Runnable = false;
    /**
     * 使用wait/notify来实现
     */
    private static void testWaitNotify() {
        // 创建t1线程
        Thread t1 = new Thread(() -> {
            synchronized (Sync_SequentialControl2.class) {
                while (!t1Runnable) {
                    try {
                        Sync_SequentialControl2.class.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("{} is runnable",Thread.currentThread().getName());
                t2Runnable = true;
                Sync_SequentialControl2.class.notifyAll();
            }
        },"t1");

        // 创建t2线程
        Thread t2 = new Thread(() -> {
            synchronized (Sync_SequentialControl2.class) {
                while (!t2Runnable) {
                    try {
                        Sync_SequentialControl2.class.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("{} is runnable",Thread.currentThread().getName());
                t3Runnable = true;
                Sync_SequentialControl2.class.notifyAll();
            }
        },"t2");

        // 创建t3线程
        Thread t3 = new Thread(() -> {
            synchronized (Sync_SequentialControl2.class) {
                while (!t3Runnable) {
                    try {
                        Sync_SequentialControl2.class.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("{} is runnable",Thread.currentThread().getName());
            }
        },"t3");

        t1.start();
        t2.start();
        t3.start();

        // 主线程作为程序的发起者，首先将t1的运行条件置为true
        Sleeper.sleep(1);
        log.debug("开始...");
        synchronized (Sync_SequentialControl2.class) {
            t1Runnable = true;
            Sync_SequentialControl2.class.notifyAll();
        }
    }

    /**
     * 对testCompletableFuture()代码的优化：
     */
    private static void testCompletableFutureApprove() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(new MyThread("t1")).thenRun(new MyThread("t2")).thenRun(new MyThread("t3"));
        future.get();
    }

    /**
     * 使用CompletableFuture来实现：java8引入了CompletableFuture，它是一个用于异步编程的强大工具。CompletableFuture提供了一系列的方法，可以用来创建，组合，转换和管理异步任务，
     *                              并且可以让你实现异步流水线，在多个任务之间轻松传递结果。
     */
    private static void testCompletableFuture() {
        // 创建CompletableFuture对象
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(new MyThread("t1"));
        // 等待future1运行结束
        future1.join();

        // 创建CompletableFuture对象
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(new MyThread("t2"));
        // 等待future2运行结束
        future2.join();

        // 创建CompletableFuture对象
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(new MyThread("t3"));
        // 等待future3运行结束
        future3.join();
    }

    /**
     * 使用线程池：线程池内部是使用了队列来存储任务的，所以线程的执行顺序会按照任务的提交顺序执行的。但是如果是多个线程同时执行的话，是保证不了先后顺序的，因为可能先提交的后执行了。
     *          但是我们可以定义一个只有一个线程的线程池，然后依次的将t1，t2，t3提交给他执行，这样就可以保证线程的执行顺序了。
     */
    private static void testThreadPool() {
        // 创建线程池
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // 创建并启动线程t1
        executor.submit(new MyThread("t1"));

        // 创建并启动线程t2
        executor.submit(new MyThread("t2"));

        // 创建并启动线程t3
        executor.submit(new MyThread("t3"));

        // 关闭线程池
        executor.shutdown();


    }

    /**
     * 基于相同的原理：还可以使用Semaphore来实现
     * Semaphore：是一个用于控制对某些资源的访问权限的同步工具类。与其他同步工具类（如 CountDownLatch 和 CyclicBarrier）不同，Semaphore 主要用于限制对某个资源的并发访问数量。
     */
    private static void testSemaphore() throws InterruptedException {
        // 创建一个具有初始计数1的Semaphore
        Semaphore semaphore = new Semaphore(1);

        // 等待线程t1执行完毕
        semaphore.acquire();
        // 创建并启动线程t1
        Thread t1 = new Thread(new MyThread(semaphore),"t1");
        t1.start();

        // 等待线程t2执行完毕
        semaphore.acquire();
        // 创建并启动线程t1
        Thread t2 = new Thread(new MyThread(semaphore),"t2");
        t2.start();

        // 等待线程t3执行完毕
        semaphore.acquire();
        // 创建并启动线程t3
        Thread t3 = new Thread(new MyThread(semaphore),"t3");
        t3.start();
    }

    /**
     * 基于相同的原理：还可以使用CyclicBarrier来实现。
     * CyclicBarrier：是一个同步工具类，允许一组线程相互等待，直到所有线程都到达某个公共的屏障点。与 CountDownLatch 不同，CyclicBarrier 可以重复使用，而 CountDownLatch 一旦计数到达零，就不能再重置。
     */
    private static void testCyclicBarrier() throws BrokenBarrierException, InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(2);

        // 创建线程t1
        Thread t1 = new Thread(new MyThread(barrier), "t1");
        t1.start();
        // 主线程阻塞，等待t1执行完
        barrier.await();

        // 创建线程t2
        Thread t2 = new Thread(new MyThread(barrier), "t2");
        t2.start();
        // 主线程阻塞，等待t2执行完
        barrier.await();

        // 创建线程t3
        Thread t3 = new Thread(new MyThread(barrier), "t3");
        t3.start();
        // 主线程阻塞，等待t3执行完
        barrier.await();

    }

    /**
     * 使用CountDownLatch实现:主要就是想办法让编排三个子线程的主线程阻塞，保证t1执行完再启动t2，t2执行完再启动t3。而这个编排的方式就是想办法知道什么时候子线程执行完，就可以通过CountDownLatch来实现。
     */
    public static void testCountDownLatch() throws InterruptedException {
        // 创建CountDownLatch对象，用来做线程之间的通信
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        CountDownLatch latch3 = new CountDownLatch(1);

        // 创建线程t1
        Thread t1 = new Thread(new MyThread(latch1), "t1");
        t1.start();
        // 主线程阻塞，等待t1执行完
        latch1.await();

        // 创建线程t2
        Thread t2 = new Thread(new MyThread(latch2), "t2");
        t2.start();
        // 主线程阻塞，等待t2执行完
        latch2.await();

        // 创建线程t2
        Thread t3 = new Thread(new MyThread(latch3), "t3");
        t3.start();
        // 主线程阻塞，等待t3执行完
        latch3.await();
    }

    static class MyThread implements Runnable {
        private CountDownLatch countDownLatch;

        private CyclicBarrier cyclicBarrier;

        private Semaphore semaphore;

        private String name;

        public MyThread() {

        }

        public MyThread(String name) {
            this.name = name;
        }

        public MyThread(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        public MyThread(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        public MyThread(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                log.debug("{} is running...", name);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                //  每执行完一个线程，计数器减一
                // countDownLatch.countDown();

                // try {
                //     cyclicBarrier.await();
                // } catch (Exception e) {
                //     throw new RuntimeException(e);
                // }

                //  释放许可证，表示完成一个线程
                // semaphore.release();
            }
        }
    }

    /**
     * 使用join实现:我们在t2中等待t1执行完毕后，再执行。然后在t3中等待t2执行完毕后，再执行。
     */
    public static void testJoin() {
        Thread t1 = new Thread(() -> {
            log.debug("{} is running...", Thread.currentThread().getName());
        }, "t1");

        Thread t2 = new Thread(() -> {
            try {
                t1.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("{} is running...", Thread.currentThread().getName());
        }, "t2");

        Thread t3 = new Thread(() -> {
            try {
                t2.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("{} is running...", Thread.currentThread().getName());
        }, "t3");

        t1.start();
        t2.start();
        t3.start();


    }
}
