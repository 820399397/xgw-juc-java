package cn.xuguowen.juc.reentrantlock;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: TestReentrantLock1
 * Package: cn.xuguowen.juc.reentrantlock
 * Description:测试ReentrantLock的可超时
 *
 * @Author 徐国文
 * @Create 2024/6/12 13:12
 * @Version 1.0
 */
@Slf4j(topic = "c.TestReentrantLock3")
public class TestReentrantLock3 {

    public static ReentrantLock lock = new ReentrantLock();


    public static void main(String[] args) {
        // 测试lock.tryLock()方法,返回值是boolean类型。返回false表示获取锁失败
        // test1();

        // 测试lock.tryLock(long time, TimeUnit unit)方法,返回值是boolean类型。返回false表示获取锁失败
        test2();
    }

    private static void test1() {
        Thread t1 = new Thread(() -> {
            log.debug("t1线程尝试获取锁资源");

            if (!lock.tryLock()) {
                log.debug("t1线程获取锁失败");
                return;
            }

            try {
                log.debug("t1线程获取到了锁资源");
            } finally {
                lock.unlock();
            }
        }, "t1");

        // 主线程获取锁,并没有释放锁资源，所以 t1线程获取锁失败
        lock.lock();
        log.debug("主线程获取到了锁");

        t1.start();

    }

    private static void test2() {
        Thread t1 = new Thread(() -> {
            log.debug("t1线程尝试获取锁资源");

            try {
                if (!lock.tryLock(2,TimeUnit.SECONDS)) {
                    log.debug("t1线程获取锁失败");
                    return;
                }
            } catch (InterruptedException e) {
                // 表示也是可以被其他线程打断的，打断之后就无法获取锁资源了，所以直接return 结束程序
                e.printStackTrace();
                return;
            }

            try {
                log.debug("t1线程获取到了锁资源");
            } finally {
                lock.unlock();
            }
        }, "t1");

        // 主线程获取锁,并没有释放锁资源，所以 t1线程获取锁失败
        lock.lock();
        log.debug("主线程获取到了锁");

        t1.start();

        // 主线程睡眠1s之后释放锁资源.t1线程尝试获取锁资源的时间是2s
        try {
            Sleeper.sleep(1);
        } finally {
            log.debug("主线程释放锁资源");
            lock.unlock();
        }
    }


}
