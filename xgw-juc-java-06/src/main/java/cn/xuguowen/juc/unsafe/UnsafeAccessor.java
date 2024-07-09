package cn.xuguowen.juc.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * ClassName: UnsafeAccessor
 * Package: cn.xuguowen.juc.unsafe
 * Description:
 *
 * @Author 徐国文
 * @Create 2024/7/9 14:14
 * @Version 1.0
 */
public class UnsafeAccessor {
    static Unsafe unsafe;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) { // 多重异常捕获的语法,java7引入的
            throw new Error(e);
        }
    }

    static Unsafe getUnsafe() {
        return unsafe;
    }
}
