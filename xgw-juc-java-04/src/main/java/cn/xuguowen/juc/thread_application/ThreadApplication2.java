package cn.xuguowen.juc.thread_application;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadApplication
 * Package: cn.xuguowen.juc.thread_application
 * Description:面向对象的方式改进 cn.xuguowen.juc.thread_application.ThreadApplication1 类的代码。
 *
 * @Author 徐国文
 * @Create 2024/5/25 10:22
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadApplication2")
public class ThreadApplication2 {

    public static void main(String[] args) throws InterruptedException {
        // room对象被2个线程同时访问，他内部的成员变量也被这两个线程同时访问并发生了读写操作，所以是存在线程安全问题的。
        // 由于 count 变量是共享的，多个线程同时对其进行读写操作会导致数据竞争（race condition），最终会出现不一致的结果。
        // 通过 synchronized 关键字来确保对 count 变量的访问是线程安全的。
        // 具体来说，你在 increment、decrement 和 getCount 方法中使用 synchronized 块对 this 对象进行同步。这样，确保同一时间只有一个线程可以进入这三个方法中的任何一个，从而避免了数据竞争问题。
        Room room = new Room();

        // 线程 t1 在不断调用 room.increment() 方法，对 count 进行加操作
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                room.increment();
            }
        }, "t1");

        // 线程 t2 在不断调用 room.decrement() 方法，对 count 进行减操作。
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                room.decrement();
            }
        }, "t2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        log.debug("count:{}", room.getCount());
    }
}

class Room {
    private int count = 0;

    public void increment() {
        synchronized (this) {
            count++;
        }
    }

    public void decrement() {
        synchronized (this) {
            count--;
        }
    }

    public int getCount() {
        synchronized (this) {
            return count;
        }
    }
}

