package cn.xuguowen.juc.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * ClassName: TestUnsafe
 * Package: cn.xuguowen.juc.unsafe
 * Description:测试获取Unsafe对象实例。
 * Unsafe:是 Java 中的一个类，位于 sun.misc 包中。它提供了一组底层的、低级别的操作，可以直接访问内存和线程调度等功能。
 * 由于这些操作绕过了Java的安全性检查和内存管理机制，所以被称为 "Unsafe"。这个类主要用于一些高性能库和框架中，特别是在需要进行一些直接内存操作时。
 * 前面学过的一些原子性操作，都是通过cas实现线程安全的。其实底层就是使用的unsafe类。
 *
 * @Author 徐国文
 * @Create 2024/7/9 13:26
 * @Version 1.0
 */
public class TestUnsafe {
    public static void main(String[] args) {
        // 错误的获取方式
        // testError();

        // 正确的获取方式：使用反射的方式获取
        Unsafe unsafe = getUnsafe();
        System.out.println(unsafe);
    }

    private static Unsafe getUnsafe() {
        try {
            // 通过反射获取 Unsafe 的单例实例
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 直接调用 Unsafe.getUnsafe() 方法通常会引发 SecurityException，
     * 因为 Unsafe 类是专门设计用于 Java 库的内部使用。为了在应用程序中使用 Unsafe，我们需要通过反射来获取 Unsafe 实例。
     */
    private static void testError() {
        Unsafe unsafe = Unsafe.getUnsafe();
        System.out.println(unsafe);
    }
}


