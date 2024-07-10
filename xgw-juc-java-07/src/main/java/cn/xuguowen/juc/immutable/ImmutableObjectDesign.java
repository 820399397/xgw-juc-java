package cn.xuguowen.juc.immutable;

/**
 * ClassName: ImmutableObjectDesign
 * Package: cn.xuguowen.juc.immutable
 * Description:不可变对象的设计。我们以更为熟悉的String类为例，说明一个不可变对象设计的要素：
 *  1.类中的属性用final修饰，属性用 final 修饰保证了该属性是只读的，不能修改。
 *  2.类中没有setter方法，因为setter方法会破坏对象的不可变性。
 *  3.类用 final 修饰保证了该类中的方法不能被覆盖，防止子类无意间破坏不可变性
 *  4.保护性拷贝：比如说String类里的substring()方法.查阅源码发现底层有这样一段代码：this.value = Arrays.copyOfRange(value, offset, offset+count);
 *    这种通过创建副本对象来避免共享的手段称之为【保护性拷贝（defensive copy）】
 * @Author 徐国文
 * @Create 2024/7/10 16:28
 * @Version 1.0
 */
public class ImmutableObjectDesign {

    public static void main(String[] args) {
        String s = "hello";
        String substring = s.substring(1);
    }
}
