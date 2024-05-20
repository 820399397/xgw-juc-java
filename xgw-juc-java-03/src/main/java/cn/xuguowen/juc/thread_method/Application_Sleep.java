package cn.xuguowen.juc.thread_method;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Application_Sleep
 * Package: cn.xuguowen.juc.thread_method
 * Description:sleep()方法的应用
 * 在没有利用 cpu 来计算时，不要让 while(true) 空转浪费 cpu，这时可以使用 yield 或 sleep 来让出 cpu 的使用权给其他程序。
 * 没有使用sleep()方法导致CPU空转的场景，可以在单核虚拟机上进行验证。使用top命令可以看到CPU使用率一直接近为100%。
 *
 * 也可以用 wait 或 条件变量达到类似的效果，不同的是，后两种都需要加锁，并且需要相应的唤醒操作，一般适用于要进行同步的场景。
 * sleep 适用于无需锁同步的场景
 * @Author 徐国文
 * @Create 2024/5/20 12:30
 * @Version 1.0
 */
@Slf4j(topic = "c.Application_Sleep")
public class Application_Sleep {

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (; ; ) {
                Sleeper.sleep(1);
                System.out.println(1);
            }
        }, "t1");

        t1.start();
    }

}
