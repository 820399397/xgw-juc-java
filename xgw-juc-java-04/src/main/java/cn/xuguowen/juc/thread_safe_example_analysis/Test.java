package cn.xuguowen.juc.thread_safe_example_analysis;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * ClassName: Test
 * Package: cn.xuguowen.juc.thread_safe_example_analysis
 * Description:分析如下代码中的成员变量是否是线程安全的。
 * 1.请比较 JDK 中 String 类的实现
 *   当前案例中由于重写父类中的方法，扩展其行为导致线程不安全。
 *   所以占在这个角度来理解String类为什么被final修饰。就是怕子类覆盖其行为，造成一些意想不到的结果。
 *
 * @Author 徐国文
 * @Create 2024/5/27 12:59
 * @Version 1.0
 */
public abstract class Test {
    public void bar() {
        // 是否安全
        // SimpleDateFormat 是线程不安全的。它的实例变量在多线程环境中会被共享和修改，从而导致并发问题。你的代码中，sdf 对象在多个线程中同时被调用，这会引发线程安全问题。
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 1.可以使用 ThreadLocal 为每个线程提供独立的 SimpleDateFormat 实例。
        // ThreadLocal<SimpleDateFormat> threadLocalSdf = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 2.每次创建新的 SimpleDateFormat 实例：在需要的时候创建新的 SimpleDateFormat 实例，而不是共享一个实例。

        // 3.使用 java.time 包中的线程安全类：使用 java.time.format.DateTimeFormatter，这是一个线程安全的类，推荐用于日期和时间的格式化和解析。
        //  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        foo(sdf);
    }

    protected abstract void foo(SimpleDateFormat sdf);

    public static void main(String[] args) {
        // 主线程执行bar()的时候，内部创建了一个SimpleDateFormat 实例。
        // 但是在执行foo()的时候，调用的是TestSubClass类中的foo()方法，他内部又新开了一个线程去操作SimpleDateFormat 实例，这样就导致线程不安全
        new TestSubClass().bar();
    }
}

class TestSubClass extends Test {

    @Override
    protected void foo(SimpleDateFormat sdf) {
        String dateStr = "1999-10-11 00:00:00";
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    sdf.parse(dateStr);
                    // threadLocalSdf.get().parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
