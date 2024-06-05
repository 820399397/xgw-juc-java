
package cn.xuguowen.juc.wait_notify.correct_posture;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Test1
 * Package: cn.xuguowen.juc.wait_notify.correct_posture
 * Description:通过5个案例代码，演示wait和notify的正确使用姿势
 * 这个案例才终于引出来wait和notify的正确使用姿势。
 *
 * @Author 徐国文
 * @Create 2024/6/5 12:17
 * @Version 1.0
 */
@Slf4j(topic = "c.Test4")
public class Test5 {
    // 锁对象
    static final Object LOCK = new Object();

    // 条件变量：分别表示送烟和送外卖的条件
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    public static void main(String[] args) {
        test1();

    }


    /**
     * 解决方法：用 while + wait，当条件不成立，再次 wait
     */
    private static void test1() {
        // 小南线程干活的前提条件是有烟.没烟就去等待了
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("有烟没？[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
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

        // 小南线程干活的前提条件是有外卖送到了.没外卖就去等待了
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("外卖送到没？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                log.debug("外卖送到没？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小女").start();

        // 主线程睡眠1s后，再启动一个送外卖线程去送外卖
        Sleeper.sleep(1);
        new Thread(() -> {
            synchronized (LOCK) {
                hasTakeout = true;
                log.debug("外卖到了奥！");
                LOCK.notifyAll();
            }
        },"送外卖的").start();
    }
}
