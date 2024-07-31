package cn.xuguowen.juc.application;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: ScheduledApplication
 * Package: cn.xuguowen.juc.application
 * Description: 应用之定时任务：如何让每周四 12:00:00 定时执行任务？
 *
 * @Author 徐国文
 * @Create 2024/7/24 13:27
 * @Version 1.0
 */
@Slf4j(topic = "c.ScheduledApplication")
public class ScheduledApplication {

    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        // initialDelay：定时任务启动后，第一次执行是什么时候

        // 1.获取到当前时间
        LocalDateTime now = LocalDateTime.now();
        // 2.获取本周四的 18:00:00
        LocalDateTime thursday = now.with(DayOfWeek.THURSDAY).withHour(12).withMinute(0).withSecond(0).withNano(0);
        // 3.如果当前时间已经超过 本周四的18:00:00，那么找下周四
        if (now.isAfter(thursday)) {
            thursday = thursday.plusWeeks(1);
        }
        // 4.计算时间差，即延时执行时间,也就是任务启动后第一次的执行时间
        long initialDelay = Duration.between(now, thursday).toMillis();
        System.out.println(initialDelay);
        // period：定时任务的执行周期，单位是毫秒
        long period = 7 * 24 * 60 * 60 * 1000;

        executorService.scheduleAtFixedRate(() -> {
            log.debug("定时任务执行：每周四 12:00:00 定时执行任务");
        },initialDelay,period, TimeUnit.MILLISECONDS);
    }
}
