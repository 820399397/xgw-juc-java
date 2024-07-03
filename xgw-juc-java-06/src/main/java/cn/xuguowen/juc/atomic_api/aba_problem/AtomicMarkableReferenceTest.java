package cn.xuguowen.juc.atomic_api.aba_problem;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * ClassName: AtomicMarkableReferenceTest
 * Package: cn.xuguowen.juc.atomic_api.aba_problem
 * Description:AtomicStampedReference 可以给原子引用加上版本号，追踪原子引用整个的变化过程，如： A -> B -> A ->C ，通过AtomicStampedReference，我们可以知道，引用变量中途被更改了几次。
 * 但是有时候，并不关心引用变量更改了几次，只是单纯的关心是否更改过，所以就有了AtomicMarkableReference
 *
 * @Author 徐国文
 * @Create 2024/7/3 14:06
 * @Version 1.0
 */
@Slf4j(topic = "c.AtomicMarkableReferenceTest")
public class AtomicMarkableReferenceTest {

    public static void main(String[] args) {
        GarbageBag bag = new GarbageBag("装满了垃圾");
        // 参数2 mark 可以看作一个标记，表示垃圾袋满了
        AtomicMarkableReference<GarbageBag> ref = new AtomicMarkableReference<>(bag, true);

        log.debug("主线程 start...");
        GarbageBag prev = ref.getReference();
        log.debug(prev.toString());

        new Thread(() -> {
            log.debug("打扫卫生的线程 start...");
            bag.setDesc("空垃圾袋");
            ref.compareAndSet(bag, bag, true, false);
            log.debug(bag.toString());
        }).start();

        Sleeper.sleep(1);

        log.debug("主线程想换一只新垃圾袋？");
        boolean success = ref.compareAndSet(prev, new GarbageBag("空垃圾袋"), true, false);
        log.debug("换了么？" + success);
        log.debug(ref.getReference().toString());
    }
}

class GarbageBag {
    String desc;

    public GarbageBag(String desc) {
        this.desc = desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return super.toString() + " " + desc;
    }
}
