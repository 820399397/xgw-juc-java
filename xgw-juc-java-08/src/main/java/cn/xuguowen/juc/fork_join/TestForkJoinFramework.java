package cn.xuguowen.juc.fork_join;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * ClassName: TestForkJoinFramework
 * Package: cn.xuguowen.juc.fork_join
 * Description:测试 fork/join 框架的使用
 * 1.Java的Fork/Join框架是一种并行处理框架，它主要用于分而治之的任务。通过把大任务拆分成小任务并行执行，然后再把结果合并，可以显著提升多核处理器的性能。Fork/Join框架在Java 7中引入，位于java.util.concurrent包中。
 * 2.核心概念：
 *  - Fork/Join任务：框架的基础是ForkJoinTask，它有两个子类：RecursiveTask<V>（有返回值）和RecursiveAction（无返回值）。
 *  - ForkJoinPool：任务执行的线程池。它是一个专门为Fork/Join任务设计的线程池，使用工作窃取算法来有效地分配和执行任务。
 *  - 工作窃取：在ForkJoinPool中，每个工作线程都有一个双端队列来存储任务。当一个线程完成了自己的任务后，可以从其他线程的队列尾部窃取任务继续执行，提高了线程的利用率。
 * 3.Fork/Join 默认会创建与 cpu 核心数大小相同的线程池
 *
 * @Author 徐国文
 * @Create 2024/7/31 10:14
 * @Version 1.0
 */
@Slf4j(topic = "c.TestForkJoinFramework")
public class TestForkJoinFramework {

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        Integer result = forkJoinPool.invoke(new MyBetterTask(1,5));
        log.debug("1~{}的和为: {}", 5, result);
    }
}

