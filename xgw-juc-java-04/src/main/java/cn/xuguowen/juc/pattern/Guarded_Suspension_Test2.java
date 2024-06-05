package cn.xuguowen.juc.pattern;

import cn.xuguowen.juc.utils.Downloader;
import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * ClassName: Guarded_Suspension_Test2
 * Package: cn.xuguowen.juc.pattern
 * Description:同步模式之保护性暂停
 *
 * 当前案例：主线程使用join方法等待子线程执行完毕。
 * 使用join的局限性：主线程必须等待子线程执行完毕所有操作才能继续执行，如果子线程执行时间过长，主线程会一直等待，导致主线程阻塞。
 * 使用保护性暂停模式，即使下载任务的线程全部工作没有执行完毕，但是下载任务完成了，就可以通知其他线程获取结果了。
 *
 * @Author 徐国文
 * @Create 2024/6/5 21:55
 * @Version 1.0
 */
@Slf4j(topic = "c.Guarded_Suspension_Test2")
public class Guarded_Suspension_Test2 {

    public static void main(String[] args) throws InterruptedException {
        GuardedSuspension2 obj = new GuardedSuspension2();

        Thread t1 = new Thread(() -> {
            try {
                log.info("t2 开始执行下载任务……");
                List<String> response = Downloader.download();
                obj.complete(response);

                // 这就是join的缺点
                Sleeper.sleep(2);
                log.info("t1 继续一些其他的工作");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "t1");

        t1.start();


        log.info("主线程 等待获取结果……");
        t1.join();
        Object response = obj.getResponse();
        if (response instanceof List) {
            log.info("主线程 获取到结果:{}", ((List<String>) response).size());
        }
    }
}

class GuardedSuspension2 {
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