package cn.xuguowen.juc.fork_join;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RecursiveTask;

/**
 * ClassName: MyBetterTask
 * Package: cn.xuguowen.juc.fork_join
 * Description:对MyTask任务进行优化：合理的拆分任务，减少递归次数
 *
 * @Author 徐国文
 * @Create 2024/7/31 10:28
 * @Version 1.0
 */
@Slf4j(topic = "c.MyBetterTask")
public class MyBetterTask extends RecursiveTask<Integer> {

    private int begin;
    private int end;

    public MyBetterTask(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String toString() {
        return "{" + begin + "," + end + '}';
    }

    @Override
    protected Integer compute() {
        if (begin == end) {
            log.debug("join() {}", begin);
            return begin;
        }

        if (end - begin == 1) {
            log.debug("join() {} + {} = {}", begin, end, end + begin);
            return end + begin;
        }

        int mid = (begin + end) / 2;    // 3
        MyBetterTask leftTask = new MyBetterTask(begin, mid);       // 1~3              1~2
        MyBetterTask rightTask = new MyBetterTask(mid + 1, end);    // 4~5              3~3

        leftTask.fork();
        rightTask.fork();
        log.debug("fork() {} + {} = ?", leftTask, rightTask);

        int result = leftTask.join() + rightTask.join();
        log.debug("join() {} + {} = {}", leftTask, rightTask, result);

        return result;
    }
}
