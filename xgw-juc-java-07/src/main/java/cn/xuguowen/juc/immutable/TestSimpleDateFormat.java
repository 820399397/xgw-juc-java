package cn.xuguowen.juc.immutable;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * ClassName: TestSimpleDateFormat
 * Package: cn.xuguowen.juc.immutable
 * Description:测试可变类SimpleDateFormat的线程安全。
 *
 * @Author 徐国文
 * @Create 2024/7/10 13:53
 * @Version 1.0
 */
@Slf4j(topic = "c.TestSimpleDateFormat")
public class TestSimpleDateFormat {

    public static void main(String[] args) {
        // test1();

        // test2();

        test3();
    }

    /**
     * 使用jdk8提供的不可变类DateTimeFormatter来解决
     * DateTimeFormatter类的源码中有这样的一段描述：This class is immutable and thread-safe.
     */
    private static void test3() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    log.debug("{}", formatter.parse("1951-04-21"));
                } catch (Exception e) {
                    log.error("{}", e);
                }
            }).start();
        }
    }

    /**
     * 使用同步锁解决线程安全问题:效率差
     */
    private static void test2() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                synchronized (sdf) {
                    try {
                        log.debug("{}", sdf.parse("1951-04-21"));
                    } catch (Exception e) {
                        log.error("{}", e);
                    }
                }
            }).start();
        }
    }

    /**
     * SimpleDateFormat是线程不安全的，多个线程对其访问会出现异常。java.lang.NumberFormatException
     */
    private static void test1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    log.debug("{}", sdf.parse("1951-04-21"));
                } catch (Exception e) {
                    log.error("{}", e);
                }
            }).start();
        }
    }
}
