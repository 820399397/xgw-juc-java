package cn.xuguowen.juc.atomic_api.aba_problem;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

/**
 * ClassName: ABAProblemTest
 * Package: cn.xuguowen.juc.atomic_api.aba_problem
 * Description:ABA问题的测试类
 *
 * @Author 徐国文
 * @Create 2024/7/3 12:50
 * @Version 1.0
 */
@Slf4j(topic = "c.ABAProblemTest")
public class ABAProblemTest {

    static AtomicReference<String> ref = new AtomicReference<>("A");

    public static void main(String[] args) {
        // test1();

        test2();
    }

    /**
     * 主线程仅能判断出共享变量的值与最初值 A 是否相同，不能感知到这种从 A 改为 B 又 改回 A 的情况，如果主线程
     * 希望：
     * 只要有其它线程【动过了】共享变量，那么自己的 cas 就算失败，这时，仅比较值是不够的，需要再加一个版本号.就需要使用到AtomicStampedReference
     */
    private static void test2() {
        log.debug("main start...");
        // 获取值 A
        String prev = ref.get();

        // 方法内部有两个线程
        // t1 将A 改为 B
        // t2 将B 改为 A
        other();

        Sleeper.sleep(1);
        // 尝试从A改为 C。虽然此次可以cas成功修改。但是main线程并不清楚这中间有没有其他线程修改过ref.所以这就是是ABA问题。
        log.debug("change A->C {}", ref.compareAndSet(prev, "C"));
    }

    private static void test1() {
        log.debug("main start...");
        // 获取值 A
        String prev = ref.get();
        Sleeper.sleep(1);
        // 尝试改为 C
        log.debug("change A->C {}", ref.compareAndSet(prev, "C"));
    }


    private static void other() {
        new Thread(() -> {
            log.debug("change A->B {}", ref.compareAndSet(ref.get(), "B"));
        }, "t1").start();
        Sleeper.sleep(0.5);
        new Thread(() -> {
            log.debug("change B->A {}", ref.compareAndSet(ref.get(), "A"));
        }, "t2").start();
    }
}
