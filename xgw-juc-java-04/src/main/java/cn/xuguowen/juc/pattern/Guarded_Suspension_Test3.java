package cn.xuguowen.juc.pattern;

import cn.xuguowen.juc.utils.Downloader;
import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * ClassName: Guarded_Suspension_Test3
 * Package: cn.xuguowen.juc.pattern
 * Description:同步模式之保护性暂停
 * <p>
 * 当前案例：t1线程带有时限的等待t2线程执行完毕后并返回结果。
 *
 * @Author 徐国文
 * @Create 2024/6/5 22:15
 * @Version 1.0
 */
@Slf4j(topic = "c.Guarded_Suspension_Test3")
public class Guarded_Suspension_Test3 {

    // 测试等待超时了，我只等你1s钟，这1s钟内你没返回，我就结束了，不等待了
    // static long timeout = 1000;

    // 测试等待没有超时，t2线程下载的速度大概是4s。我等待你10s钟，你给我返回结果，我继续执行
    static long timeout = 10000;

    public static void main(String[] args) {
        GuardedSuspension3 obj = new GuardedSuspension3();

        new Thread(() -> {
            log.info("t1等待获取结果……");
            Object response = obj.getResponseImprove(timeout);
            log.info("t1 获取到结果:{}", response);
        },"t1").start();

        new Thread(() -> {
            try {
                log.info("t2 开始执行下载任务……");
                List<String> response = Downloader.download();
                // 测试虚假唤醒 + 等待没有超时：传递参数null
                // 此时输出的结果为：t1线程仍然会等待10s钟的
                obj.complete(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        },"t2").start();
    }
}

@Slf4j(topic = "c.GuardedSuspension3")
class GuardedSuspension3 {
    // 结果
    private Object response;

    // 获取结果：对如下获取结果代码的优化
    public Object getResponseImprove(long timeout) {
        synchronized (this) {
            // 等待的开始时间 15:00:00
            long begin = System.currentTimeMillis();

            // 要经历的时间
            long passedTime = 0;

            while (response == null) {
                // 其实 timeout - passedTime 就是我还要等待的时间
                long waitTime = timeout - passedTime;
                log.debug("waitTime: {}", waitTime);
                if (waitTime <= 0) {
                    log.debug("break...");
                    break;
                }
                try {
                    // 假设 timeout = 2000 也就是2s
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 计算出经历的时间 15:00:01,这种情况下就是提前被唤醒了
                passedTime = System.currentTimeMillis() - begin;
                log.debug("timePassed: {}, object is null {}",passedTime,response == null);
            }
            return response;
        }
    }

    // 获取结果
    public Object getResponse(long timeout) {
        synchronized (this) {
            // 等待的开始时间 15:00:00
            long begin = System.currentTimeMillis();

            // 经历的时间
            long passedTime = 0;

            while (response == null) {
                if (passedTime >= timeout) {
                    break;
                }
                try {
                    // 假设 timeout = 2000 也就是2s
                    this.wait(timeout - passedTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 计算出经历的时间 15:00:01
                passedTime = System.currentTimeMillis() - begin;
            }
            return response;
        }
    }

    // 生成结果
    public void complete(Object response) {
        synchronized (this) {
            this.response = response;
            log.info("notify……");
            this.notifyAll();
        }
    }
}