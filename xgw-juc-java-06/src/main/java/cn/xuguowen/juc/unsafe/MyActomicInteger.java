package cn.xuguowen.juc.unsafe;

import sun.misc.Unsafe;

/**
 * ClassName: MyActomicInteger
 * Package: cn.xuguowen.juc.unsafe
 * Description:基于unsafe类实现自定义原子整数类。jdk中AtomicInteger底层的源码就是这样的。
 *
 * @Author 徐国文
 * @Create 2024/7/9 14:12
 * @Version 1.0
 */
public class MyActomicInteger {

    private volatile int value;

    private static final long valueOffset;
    private static final Unsafe UNSAFE;

    static {
        try {
            // 1.获取unsafe对象
            UNSAFE = UnsafeAccessor.getUnsafe();
            // 2.获取value属性的偏移量
            valueOffset = UNSAFE.objectFieldOffset(MyActomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public MyActomicInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void decrease(int amount) {
        while (true) {
            int prev = this.value;
            int next = this.value - amount;
            if (UNSAFE.compareAndSwapInt(this, valueOffset, prev, next)) {
                break;
            }
        }
    }
}
