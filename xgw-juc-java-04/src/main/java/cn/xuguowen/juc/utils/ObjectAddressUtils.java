package cn.xuguowen.juc.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * ClassName: ObjectAddressUtils
 * Package: cn.xuguowen.juc.utils
 * Description:输出对象头markword的二进制数据
 *
 * @Author 徐国文
 * @Create 2024/6/3 21:56
 * @Version 1.0
 */
public class ObjectAddressUtils {



    private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    public static String getAddress(Object obj) throws Exception {
        // 获取Unsafe实例
        Unsafe unsafe = getUnsafe();

        // 获取对象头的markword
        long markWord = unsafe.getLong(obj, 0L);

        // 输出markword的二进制表示
        return "Mark Word (in binary): " + String.format("%64s", Long.toBinaryString(markWord)).replace(' ', '0');
    }

    public static void main(String[] args) throws Exception {
        Object o = new Object();
        String address = getAddress(o);
        System.out.println(address);
    }
}
