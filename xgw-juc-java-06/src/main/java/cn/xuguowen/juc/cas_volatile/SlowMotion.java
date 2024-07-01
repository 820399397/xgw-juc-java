package cn.xuguowen.juc.cas_volatile;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName: SlowMotion
 * Package: cn.xuguowen.juc.cas_volatile
 * Description:慢动作分析cas原理
 *
 * @Author 徐国文
 * @Create 2024/7/1 13:36
 * @Version 1.0
 */
@Slf4j(topic = "c.SlowMotion")
public class SlowMotion {

    public static void main(String[] args) {
        // 初始化一个 AtomicInteger 类型的变量 balance，初始值为 10000。
        AtomicInteger balance = new AtomicInteger(10000);
        // 获取 balance 的初始值 mainPrev，并记录日志。
        int mainPrev = balance.get();
        log.debug("try get {}", mainPrev);
        // 创建并启动一个新的线程 t1，在这个线程中：
        new Thread(() -> {
            // 线程休眠 1 秒钟。
            Sleeper.sleep(1);
            // 获取 balance 的当前值 prev(依然是 10000)。
            int prev = balance.get();
            // 尝试将 balance 从 prev 更新为 9000。
            balance.compareAndSet(prev, 9000);
            // 记录更新后的 balance。
            log.debug(balance.toString());
        }, "t1").start();
        // 主线程休眠 2 秒钟。
        Sleeper.sleep(2);
        // 尝试将 balance 从 mainPrev(依然是 10000) 更新为 8000，并记录尝试的日志。
        // 如果此时 balance 的值已经被 t1 线程更新为 9000，则此尝试会失败。
        log.debug("try set 8000...");
        boolean isSuccess = balance.compareAndSet(mainPrev, 8000);
        log.debug("is success ? {}", isSuccess);
        // 如果更新失败（因为 balance 的值已经被其他线程修改过），则重新获取 balance 的当前值，并再尝试一次更新为 8000。
        if(!isSuccess){
            // 这次将balance的最新之赋值给mainPrev
            mainPrev = balance.get();
            log.debug("try set 8000...");
            isSuccess = balance.compareAndSet(mainPrev, 8000);
            log.debug("is success ? {}", isSuccess);
        }
    }

}
