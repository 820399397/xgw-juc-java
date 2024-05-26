package cn.xuguowen.juc.variables_thread_safe;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Local_Variables
 * Package: cn.xuguowen.juc.thread_safe
 * Description:演示局部变量的线程安全
 *
 *
 * @Author 徐国文
 * @Create 2024/5/26 15:56
 * @Version 1.0
 */
@Slf4j(topic = "c.Local_Variables1")
public class Local_Variables1 {

    /**
     * 局部变量是线程安全的：集合线程的原理来理解。JVM运行的时候会为没个线程生成一个栈，线程内部调用方法则在该栈中分配一个栈帧出来。
     */
    public static void test1(){
        int i = 10;
        i++;

    }
}


