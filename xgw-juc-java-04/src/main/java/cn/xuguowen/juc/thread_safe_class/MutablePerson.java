package cn.xuguowen.juc.thread_safe_class;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: MutablePerson
 * Package: cn.xuguowen.juc.thread_safe_class
 * Description:演示如果不采取深度拷贝或返回不可变视图，会导致不可变类被破坏的情况。
 *
 * @Author 徐国文
 * @Create 2024/5/26 17:10
 * @Version 1.0
 */
// 虽然是一个不可变类，但是内部的数组是可变的。
@Slf4j(topic = "c.MutablePerson")
public final class MutablePerson {

    // array 变量是一个不可变的引用，意味着一旦分配了数组的内存空间，就无法再指向其他内存空间。
    // 但是，数组本身内部的元素值是可以被修改的，因为 final 关键字只保证了数组的引用不可变，而并没有限制数组中元素的内容。
    private final int[] array;

    public MutablePerson(int[] array) {
        // 没有进行深度拷贝:当传入的 originalArray 被修改时，MutablePerson 内部的 array 也会被修改，因为它们引用的是同一个数组对象。
        // this.array = array;
        this.array = array.clone();
    }


    public int[] getArray() {
        // 直接返回内部数组的引用:getArray 方法直接返回内部数组的引用，外部代码可以通过这个引用修改内部数组。
        return array;

        // return array.clone();
    }



    public static void main(String[] args) {
        int[] originalArray = {1, 2, 3, 4, 5};
        MutablePerson mic = new MutablePerson(originalArray);

        // 修改原始数组:原本应该不可变的 MutablePerson 的内部状态被外部修改了，破坏了不可变性。
        originalArray[0] = 99;

        // 获取内部数组的引用并修改
        // int[] internalArray = mic.getArray();
        // internalArray[1] = 88;

        // 打印结果
        log.debug("原始数组: {}",originalArray);
        log.debug("内部数组: {}",mic.getArray());
        // 输出：99 88 3 4 5
    }
}

