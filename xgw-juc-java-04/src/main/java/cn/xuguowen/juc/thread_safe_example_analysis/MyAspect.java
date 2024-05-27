package cn.xuguowen.juc.thread_safe_example_analysis;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * ClassName: MyAspect
 * Package: cn.xuguowen.juc.thread_safe_example_analysis
 * Description:分析如下代码是否是线程安全的。
 *
 * @Author 徐国文
 * @Create 2024/5/27 12:43
 * @Version 1.0
 */
@Aspect
@Component
public class MyAspect {
    // 是否安全？
    /**
     * Spring AOP 切面默认是单例的，也就是说，Spring 容器中只会有一个 MyAspect 实例，并且所有匹配的连接点（即方法调用）都会共享这个单例实例。这会导致以下线程安全问题
     * 切面实例中的 start 变量被所有线程共享。当多个线程同时执行切面逻辑时，它们会同时访问和修改 start 变量，导致 start 变量的值不确定，进而导致计算的时间差不正确。
     *
     * 解决方法
     * 1.ThreadLocal 可以确保每个线程都有自己独立的变量副本，从而避免共享状态导致的线程安全问题。
     * 2.改成环绕通知，start变量改为局部变量
     */
    private long start = 0L;

    // 使用 ThreadLocal 存储每个线程独立的 start 值
    // private ThreadLocal<Long> startThreadLocal = ThreadLocal.withInitial(() -> 0L);
    //
    // @Before("execution(* *(..))")
    // public void before() {
    //     startThreadLocal.set(System.nanoTime());
    // }
    //
    // @After("execution(* *(..))")
    // public void after() {
    //     long end = System.nanoTime();
    //     System.out.println("cost time:" + (end - start.get()));
    //     startThreadLocal.remove(); // 清除 ThreadLocal 变量
    // }

    @Before("execution(* *(..))")
    public void before() {
        start = System.nanoTime();
    }

    @After("execution(* *(..))")
    public void after() {
        long end = System.nanoTime();
        System.out.println("cost time:" + (end-start));
    }
}
