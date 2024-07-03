package cn.xuguowen.juc.atomic_api.aba_problem;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * ClassName: AtomicStampedReferenceTest
 * Package: cn.xuguowen.juc.atomic_api.aba_problem
 * Description:解决ABA问题：增加了一个状态标记位.
 * AtomicStampedReference 可以给原子引用加上版本号，追踪原子引用整个的变化过程，如： A -> B -> A ->C ，通过AtomicStampedReference，我们可以知道，引用变量中途被更改了几次。
 * AtomicStampedReference 设计用来防止 ABA 问题的机制，它通过版本号来检测和避免引用值在中途被多次修改和恢复的情况。
 * 但是有时候，并不关心引用变量更改了几次，只是单纯的关心是否更改过，所以就有了AtomicMarkableReference
 *
 * @Author 徐国文
 * @Create 2024/7/3 14:00
 * @Version 1.0
 */
@Slf4j(topic = "c.AtomicStampedReferenceTest")
public class AtomicStampedReferenceTest {

    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);

    public static void main(String[] args) {
        log.debug("main start...");
        // 获取值 A
        String prev = ref.getReference();
        // 获取版本号
        int stamp = ref.getStamp();
        log.debug("版本 {}", stamp);
        // 如果中间有其它线程干扰，发生了 ABA 现象
        other();
        Sleeper.sleep(1);
        // 尝试将A改为 C。主线程最后修改失败的原因是由于版本号的不匹配。
        log.debug("change A->C {}", ref.compareAndSet(prev, "C", stamp, stamp + 1));
    }

    private static void other() {
        new Thread(() -> {
            log.debug("change A->B {}", ref.compareAndSet(ref.getReference(), "B",
                    ref.getStamp(), ref.getStamp() + 1));
            log.debug("更新版本为 {}", ref.getStamp());
        }, "t1").start();
        Sleeper.sleep(0.5);
        new Thread(() -> {
            log.debug("change B->A {}", ref.compareAndSet(ref.getReference(), "A",
                    ref.getStamp(), ref.getStamp() + 1));
            log.debug("更新版本为 {}", ref.getStamp());
        }, "t2").start();
    }
}
