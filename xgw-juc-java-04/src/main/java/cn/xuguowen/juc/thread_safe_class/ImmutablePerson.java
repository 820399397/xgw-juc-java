package cn.xuguowen.juc.thread_safe_class;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ClassName: ImmutablePerson
 * Package: cn.xuguowen.juc.thread_safe_class
 * Description:扩展知识：当一个不可变类包含对可变对象（如数组或集合）的引用时，确保这些引用也不可变是至关重要的。
 * 因为即使不可变类的字段本身是 final 的，但如果这些字段指向的对象本身是可变的，那么外部代码仍然可以通过这些引用改变类的状态，从而破坏不可变性。
 * 确保不可变引用的常用方法包括在构造函数中进行深度拷贝或返回不可变视图。下面详细解释这两种方法.
 * 深度拷贝和不可变视图的结合使用，确保了不可变类内部引用的可变对象也具有不可变性，从而保持整个类的不可变性。
 *
 * @Author 徐国文
 * @Create 2024/5/26 17:01
 * @Version 1.0
 */
public final class ImmutablePerson {
    private final String name;
    private final int age;
    private final List<String> hobbies;

    public ImmutablePerson(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        // 深度拷贝是在创建不可变类的实例时，复制传入的可变对象，使得不可变类内部的字段不再指向原始的可变对象，而是指向一个新的、独立的副本。
        // 创建副本，确保传入的 list 不会被外部修改
        this.hobbies = new ArrayList<>(hobbies);
    }

    /**
     * 不可变视图是指通过某些机制将可变对象包装为不可变对象，并且返回给外部调用者时，确保其无法修改。
     *
     * @return
     */
    public List<String> getHobbies() {
        // 返回不可变视图.外部代码可以读取这个列表，但不能修改它。
        return Collections.unmodifiableList(hobbies);
    }


}
