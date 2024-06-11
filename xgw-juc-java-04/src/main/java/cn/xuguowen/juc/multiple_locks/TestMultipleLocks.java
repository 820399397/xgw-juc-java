package cn.xuguowen.juc.multiple_locks;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: TestMultipleLocks
 * Package: cn.xuguowen.juc.multiple_locks
 * Description:多把不相干的锁：
 * 案例1：一间大屋子有两个功能：睡觉、学习，互不相干。现在小南要学习，小女要睡觉，但如果只用一间屋子（一个对象锁）的话，那么并发度很低
 * 案例2：准备多个房间（多个对象锁）
 *
 * 这个过程叫做将锁的粒度细分：
 * 好处：通过为不同的操作使用不同的锁对象（studyRoom和bedRoom），可以提高并发度，因为不同操作的线程不会相互阻塞。
 * 潜在问题：虽然提高了并发度，但如果某个线程需要同时获取多个锁（例如：如果一个线程同时调用study()和sleep()），就可能会发生死锁。
 *
 * @Author 徐国文
 * @Create 2024/6/11 13:36
 * @Version 1.0
 */
@Slf4j(topic = "c.TestMultipleLocks")
public class TestMultipleLocks {
    public static void main(String[] args) {
        // 测试一把锁并发度低的场景
        // test1();

        test2();

    }

    /**
     * BigRoom类使用了两个独立的锁对象。
     * 这样一来，study()方法和sleep()方法可以同时进行，因为它们锁的是不同的对象。这就提高了并发度。
     */
    public static void test2() {
        BigRoom bigRoom = new BigRoom();
        // 小南线程学习
        new Thread( () -> {
            bigRoom.study();
        },"小南").start();

        // 小女线程睡觉
        new Thread(() -> {
            bigRoom.sleep();
        },"小女").start();
    }

    /**
     * 如果BigRoom类没有两个独立的锁对象（假设没有细分锁），那么调用sleep()和study()的方法都会竞争同一个锁。
     * 这会导致其中一个线程必须等待另一个线程完成后才能开始，从而降低并发度。
     */
    public static void test1() {
        BigRoom bigRoom = new BigRoom();
        // 小南线程学习
        new Thread( () -> {
            bigRoom.study();
        },"小南").start();

        // 小女线程睡觉
        new Thread(() -> {
            bigRoom.sleep();
        },"小女").start();

    }
}

@Slf4j(topic = "c.BigRoom")
class BigRoom {
    // 如下的2个变量是为了测试test2()方法新增的，测试多把不相干的锁
    private final Object studyRoom = new Object();

    private final Object bedRoom = new Object();

    public void sleep() {
        synchronized (bedRoom) {
            log.debug("sleeping 2 s");
            Sleeper.sleep(2);
        }
    }

    public void study() {
        synchronized (studyRoom) {
            log.debug("studying 1 s");
            Sleeper.sleep(1);
        }
    }
}
