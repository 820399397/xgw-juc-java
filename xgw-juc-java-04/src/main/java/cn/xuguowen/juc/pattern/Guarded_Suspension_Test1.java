package cn.xuguowen.juc.pattern;

import cn.xuguowen.juc.utils.Downloader;
import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * ClassName: Guarded_Suspension_Test
 * Package: cn.xuguowen.juc.pattern
 * Description:同步模式之保护性暂停：通常用于一个线程等待另一个线程的执行结果。这种情况下，我们希望第一个线程能够暂停自己的执行，直到第二个线程完成其工作并提供结果。
 * 在Java中，join 的实现、Future 的实现，采用的就是此模式。
 * 1.如何做：有一个结果需要从一个线程传递到另一个线程，让他们关联同一个 GuardedObject。
 * 2.区别于生产者-消费者模式：虽然都可以通过某个对象的状态来决定何时操作，但重点不同。如果有结果不断从一个线程到另一个线程那么可以使用消息队列（见生产者/消费者）
 * <p>
 * 当前案例：t1线程等待t2线程执行完毕后并返回结果后，t1线程方可继续执行，否则一直等待下去。
 *
 * @Author 徐国文
 * @Create 2024/6/5 21:27
 * @Version 1.0
 */
@Slf4j(topic = "c.Guarded_Suspension_Test1")
public class Guarded_Suspension_Test1 {

    public static void main(String[] args) {
        GuardedSuspension1 obj = new GuardedSuspension1();

        new Thread(() -> {
            log.info("t1等待获取结果……");
            Object response = obj.getResponse();
            if (response instanceof List) {
                log.info("t1 获取到结果:{}", ((List<String>) response).size());
            }
        },"t1").start();

        new Thread(() -> {
            try {
                log.info("t2 开始执行下载任务……");
                List<String> response = Downloader.download();
                obj.complete(response);

                // 相较于join方法，这是优势
                Sleeper.sleep(2);
                log.info("t2 继续一些其他的工作");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        },"t2").start();
    }

}


class GuardedSuspension1 {
    // 结果
    private Object response;

    // 获取结果
    public Object getResponse() {
        synchronized (this) {
            while (response == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return response;
        }
    }

    // 生成结果
    public void complete(Object response) {
        synchronized (this) {
            this.response = response;
            this.notifyAll();
        }
    }
}