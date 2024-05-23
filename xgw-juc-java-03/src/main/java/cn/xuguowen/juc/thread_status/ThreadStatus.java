package cn.xuguowen.juc.thread_status;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ThreadStatus
 * Package: cn.xuguowen.juc.thread_status
 * Description:验证java中是有6种状态的
 *
 * @Author 徐国文
 * @Create 2024/5/22 21:29
 * @Version 1.0
 */
@Slf4j(topic = "c.ThreadStatus")
public class ThreadStatus {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {

        }, "t1");


        Thread t2 = new Thread(() -> {
            log.debug("running...");
        },"t2");
        t2.start();

        Thread t3 = new Thread(() -> {
            while (true) {

            }
        },"t3");
        t3.start();

        Thread t4 = new Thread(() -> {
            synchronized (ThreadStatus.class) {
                try {
                    Thread.sleep(100000000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"t4");
        t4.start();


        Thread t5 = new Thread(() -> {
            try {
                t3.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },"t5");
        t5.start();

        Thread t6 = new Thread(() -> {
            synchronized (ThreadStatus.class) {
                log.debug("get lock...");
            }
        },"t6");
        t6.start();

        Thread.sleep(500);
        log.debug("t1 state：{}",t1.getState());
        log.debug("t2 state：{}",t2.getState());
        log.debug("t3 state：{}",t3.getState());
        log.debug("t4 state：{}",t4.getState());
        log.debug("t5 state：{}",t5.getState());
        log.debug("t6 state：{}",t6.getState());
    }


}
