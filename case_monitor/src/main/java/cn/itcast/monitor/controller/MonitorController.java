package cn.itcast.monitor.controller;

import cn.itcast.monitor.service.MonitorService;
import cn.itcast.monitor.vo.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author yihang
 */
@RestController
public class MonitorController {

    // ArrayBlockingQueue 是 Java 中的一种阻塞队列。阻塞队列是并发编程中的一个重要概念，它提供了线程安全的方式来在多个生产者和消费者之间传递数据。
    public static ArrayBlockingQueue<Info> QUEUE = new ArrayBlockingQueue(30);

    @Autowired
    private MonitorService monitorService;

    @GetMapping("/info")
    public List<Info> info() {
        //  创建一个ArrayList来接收队列中的元素
        ArrayList<Info> infos = new ArrayList<>();
        // 使用drainTo方法将队列中的元素转移到ArrayList中
        QUEUE.drainTo(infos);
        return infos;
    }

    @GetMapping("/start")
    public void start() {
        monitorService.start();
    }

    @GetMapping("/stop")
    public void stop() {
        monitorService.stop();
    }
}
