package cn.xuguowen.juc.atomic_api.atomicfieldupdater;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * ClassName: TestAtomicReferenceFieldUpdater
 * Package: cn.xuguowen.juc.atomic_api.atomicfieldupdater
 * Description:原子字段更新器：是一种在多线程环境中高效更新对象字段的机制。它们位于java.util.concurrent.atomic包中，提供了一种原子地更新对象字段的方法，而不需要使用同步关键字（synchronized）。字段更新器主要有以下三种类型：
 * - AtomicIntegerFieldUpdater
 * - AtomicLongFieldUpdater
 * - AtomicReferenceFieldUpdater
 * 这些字段更新器允许对对象的volatile字段进行原子更新。字段更新器的使用需要一些注意事项，如字段必须是volatile的，不能是final的，并且字段的类型和类必须匹配。
 * 使用注意事项：
 * 1.字段必须是volatile的：确保可见性。
 * 2.不能是final字段：final字段不能被更新。
 * 3.字段类型和类必须匹配：字段更新器需要知道字段的确切类型和所属类。
 *
 *
 * @Author 徐国文
 * @Create 2024/7/7 23:21
 * @Version 1.0
 */
public class TestAtomicReferenceFieldUpdater {
    public static void main(String[] args) {
        Student student = new Student();

        AtomicReferenceFieldUpdater<Student, String> updater = AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");
        boolean flag = updater.compareAndSet(student, null, "xgw");
        System.out.println(flag);// 输出 true
    }

}

class Student {
    // 如果对象中的字段没有用volatile关键字修饰，则出现如下异常：
    // Exception in thread "main" java.lang.IllegalArgumentException: Must be volatile type
    volatile String name;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Student{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}