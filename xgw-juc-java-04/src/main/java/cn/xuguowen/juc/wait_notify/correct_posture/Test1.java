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
@Slf4j(topic = "c.Test1")
public class Test1 {
    // 锁对象
    static final Object LOCK = new Object();

    // 条件变量：分别表示送烟和送外卖的条件
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    public static void main(String[] args) {
        // test1();

        test2();
    }

    /**
     * 送烟线程哪里也加了synchronized，此案例的问题是：
     * 1.小南线程根本就没有干活的
     */
    private static void test2() {
        // 小南线程干活的前提条件是有烟.没烟就去睡眠了
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    Sleeper.sleep(2);
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
            }
        },"送烟的").start();
    }

    /**
     * 问题分析：
     * 1.主线程提前1s就把烟送到了，可是小南线程还在睡眠中，并没有及时醒过来。
     * 2.其他线程不需要条件就可以干活的，目前的问题就是其他线程得等小南线程执行完毕之后才可干活。因为小南线程睡眠了，并没有释放锁资源
     */
    private static void test1() {
        // 小南线程干活的前提条件是有烟.没烟就去睡眠了
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    Sleeper.sleep(2);
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
            hasCigarette = true;
            log.debug("烟到了奥！");
        },"送烟的").start();
    }
}
