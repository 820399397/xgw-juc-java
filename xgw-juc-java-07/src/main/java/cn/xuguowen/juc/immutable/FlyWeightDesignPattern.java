package cn.xuguowen.juc.immutable;

/**
 * ClassName: FlyWeightDesignPattern
 * Package: cn.xuguowen.juc.immutable
 * Description:引出享元设计模式:享元设计模式（Flyweight Pattern）是一种结构型设计模式，用于减少内存使用量并提高性能。其核心思想是通过共享尽可能多的相似对象来节省内存，从而避免为每个对象单独创建内存空间。
 * 1.享元设计模式的体现：
 *  - 包装类：在JDK中 Boolean，Byte，Short，Integer，Long，Character 等包装类提供了 valueOf 方法，例如 Long 的valueOf 会缓存 -128~127 之间的 Long 对象，在这个范围之间会重用对象，大于这个范围，才会新建 Long 对象
 *  - String 串池
 *  - BigDecimal BigInteger:底层也是不可变的。每次操作之后都是会创建一个新的对象返回。所以说是线程安全的。
 * @Author 徐国文
 * @Create 2024/7/10 16:33
 * @Version 1.0
 */
public class FlyWeightDesignPattern {
    public static void main(String[] args) {
        // Byte, Short, Long 缓存的范围都是 -128~127
        Long aLong = Long.valueOf(12);
        // Character 缓存的范围是 0~127
        Character c = Character.valueOf('c');
        // Integer的默认范围是 -128~127。最小值是不能改变的，但最大值可以通过调整虚拟机参数来改变：-Djava.lang.Integer.IntegerCache.high来改变
        Integer integer = Integer.valueOf(12);
        // Boolean 缓存了 TRUE 和 FALSE
        Boolean aBoolean = Boolean.valueOf(true);

        // BigDecimal

    }
}
