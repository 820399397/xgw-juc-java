package cn.xuguowen.juc.wait_notify.correct_posture;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Test1
 * Package: cn.xuguowen.juc.wait_notify.correct_posture
 * Description:通过5个案例代码，演示wait和notify的正确使用姿势
 *
 * @Author 徐国文
 * @Create 2024/6/5 12:17
 * @Version 1.0
 */
@Slf4j(topic = "c.Test2")
public class Test2 {
    // 锁对象
    static final Object LOCK = new Object();

    // 条件变量：分别表示送烟和送外卖的条件
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    public static void main(String[] args) {
        test1();

    }


    /**
     * 和案例1相比，改进的地方是：解决了其它干活的线程阻塞的问题
     * 貌似当前这个案例没有问题了，但是有其它线程也在等待条件呢？@See cn.xuguowen.juc.wait_notify.correct_posture.Test3
     */
    private static void test1() {
        // 小南线程干活的前提条件是有烟.没烟就去等待了
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");

                    // 这次小南不在是睡眠了，而是进入waitSet中等待，此时他会释放锁资源的
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("有烟了，可以开始干活了");
                }
            }
        }, "小南").start();

        // 其他5个线程也在干活，没有条件就可以干活
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (LOCK) {
                    log.debug("其他人可以开始干活了");
                }
            },"其他人").start();
        }

        // 主线程睡眠1s后，再启动一个送烟线程去送烟
        Sleeper.sleep(1);
        new Thread(() -> {
            synchronized (LOCK) {
                hasCigarette = true;
                log.debug("烟到了奥！");
                // 送烟线程唤醒小南这个线程
                LOCK.notify();
            }
        },"送烟的").start();
    }
}
