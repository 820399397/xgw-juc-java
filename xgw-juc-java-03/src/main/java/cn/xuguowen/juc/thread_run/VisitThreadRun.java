package cn.xuguowen.juc.thread_run;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: VisitThreadRun
 * Package: cn.xuguowen.juc.thread_run
 * Description:观察2个线程交替执行的现象
 *
 * @Author 徐国文
 * @Create 2024/5/17 12:25
 * @Version 1.0
 */
@Slf4j(topic = "c.VisitThreadRun")
public class VisitThreadRun {

    public static void main(String[] args) {
        new Thread(() ->{
            while (true) {
                log.debug("t1 running...");
            }
        },"t1").start();

        new Thread(() ->{
            while (true) {
                log.debug("t2 running...");
            }
        },"t2").start();
    }
}
