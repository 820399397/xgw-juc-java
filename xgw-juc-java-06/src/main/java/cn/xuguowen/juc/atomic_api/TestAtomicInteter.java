package cn.xuguowen.juc.atomic_api;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

/**
 * ClassName: TestAtomicInteter
 * Package: cn.xuguowen.juc.atomic_api
 * Description:
 *
 * @Author 徐国文
 * @Create 2024/7/1 14:46
 * @Version 1.0
 */
public class TestAtomicInteter {

    public static void main(String[] args) {
        // testBasic();

        // testupdateAndGet();

        // updateAndGetPrinciple();

        AtomicInteger atomicInteger = new AtomicInteger(10);
        updateAndGetPrincipleImprove(atomicInteger,i -> i / 2);

    }

    /**
     * 使用函数式接口和lambda表达式优化updateAndGetPrinciple这个方法
     * 基于接口的思想来实现：接口定义抽象方法，具体子类如何实现那是看你自己了
     * @param atomicInteger
     * @param function
     */
    private static void updateAndGetPrincipleImprove(AtomicInteger atomicInteger,IntUnaryOperator function) {
        while (true) {
            int prev = atomicInteger.get();
            int next = function.applyAsInt(prev);
            if (atomicInteger.compareAndSet(prev, next)) {
                break;
            }
        }

        System.out.println(atomicInteger.get());
    }

    /**
     * updateAndGet方法的原理:其实底层就是采用了compareAndSet方法实现的
     * 但是目前我们自己实现的这个方法是有一个缺点的，就是方法内部限制死了操作的。在这个方法内部只能实现乘法的操作。
     */
    private static void updateAndGetPrinciple() {
        AtomicInteger atomicInteger = new AtomicInteger(10);

        while (true) {
            int prev = atomicInteger.get();
            int next = prev * 5;
            if (atomicInteger.compareAndSet(prev, next)) {
                break;
            }
        }
        System.out.println(atomicInteger.get());

    }

    private static void testHigh() {
        AtomicInteger atomicInteger = new AtomicInteger(10);

        // currentValue:实际就是AtomicInteger对象中的value属性
        // currentValue * 5:将来要更新的值，变更之后赋值到value上
        System.out.println(atomicInteger.updateAndGet(currentValue -> currentValue * 5));

        System.out.println(atomicInteger.getAndUpdate(currentValue -> currentValue * 5));

        System.out.println(atomicInteger.get());
    }

    /**
     * 测试一些基础的方法
     */
    private static void testBasic() {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        // 自增操作
        System.out.println(atomicInteger.incrementAndGet());    // ++i,i=1
        System.out.println(atomicInteger.getAndIncrement());    // i++,i=2.但是先获取到的是 1

        // 指定步长
        System.out.println(atomicInteger.getAndAdd(5));     //  i=2,再加5，返回的是2，因为先get的
        System.out.println(atomicInteger.addAndGet(5));     //  i=7,再加5，返回的是12，因为最后get的

        System.out.println(atomicInteger.get());
    }
}
