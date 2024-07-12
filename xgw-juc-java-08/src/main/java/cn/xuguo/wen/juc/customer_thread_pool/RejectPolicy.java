package cn.xuguo.wen.juc.customer_thread_pool;

/**
 * ClassName: RejectPolicy
 * Package: cn.xuguo.wen.juc.customer_thread_pool
 * Description:拒绝策略接口
 *
 * @Author 徐国文
 * @Create 2024/7/12 16:27
 * @Version 1.0
 */
@FunctionalInterface
public interface RejectPolicy<T> {
    /**
     * 任务队列满了之后的拒绝策略
     *
     * @param taskQueue 任务队列
     * @param task      任务
     */
    void reject(BlockingQueue<T> taskQueue, T task);
}
