package cn.xuguowen.juc.unsafe;

import lombok.Data;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * ClassName: TestUnsafeCAS
 * Package: cn.xuguowen.juc.unsafe
 * Description:测试Unsafe类中的CAS方法。
 *
 * @Author 徐国文
 * @Create 2024/7/9 13:42
 * @Version 1.0
 */
public class TestUnsafeCAS {

    public static void main(String[] args) throws NoSuchFieldException {
        Unsafe unsafe = getUnsafe();

        // 需求：使用Unsafe的CAS方法，更新teacher对象中的属性值
        Field id = Teacher.class.getDeclaredField("id");
        Field name = Teacher.class.getDeclaredField("name");

        // 使用unsafe对象获取对象字段的偏移量
        long idOffset = unsafe.objectFieldOffset(id);
        long nameOffset = unsafe.objectFieldOffset(name);

        // 使用CAS方法更新属性值
        Teacher teacher = new Teacher();
        boolean idFlag = unsafe.compareAndSwapInt(teacher, idOffset, 0, 1);
        boolean nameFlag = unsafe.compareAndSwapObject(teacher, nameOffset, null, "张三");
        System.out.println(teacher);
    }

    //  获取 Unsafe 实例
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
}

@Data
class Teacher {
    private int id;
    private String name;
}