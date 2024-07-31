package cn.xuguowen.juc.fork_join;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RecursiveTask;

/**
 * ClassName: MyTask
 * Package: cn.xuguowen.juc.fork_join
 * Description:
 * 计算 1~n 的和
 * 例如：计算 1~5的和。
 * 思路：你计算 1~5的和。
 * n是5，是不是先得计算出  1~4的和，然后再把5加进去。
 * 1~4的和，是不是又可以拆分成 1~3的和 和 4的和。
 * 1~3的和，是不是又可以拆分成 1~2的和 和 3的和。
 * 1~2的和，是不是又可以拆分成 1 和 2的和。
 * 直到 1 之后不可再拆分
 * @Author 徐国文
 * @Create 2024/7/31 10:26
 * @Version 1.0
 */
@Slf4j(topic = "c.MyTask")
public class MyTask extends RecursiveTask<Integer> {

    private int n;

    public MyTask(int n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "{" + n + '}';
    }

    @Override
    protected Integer compute() {
        // 如果 n 已经是 1 了，就可以求得结果了。
        if (1 == n) {
            log.debug("join() {}", n);
            return 1;
        }

        MyTask subtask = new MyTask(n - 1);
        subtask.fork();
        log.debug("fork() {} + {}", n, subtask);

        int result = n + subtask.join();
        log.debug("join() {} + {} = {}", n, subtask, result);
        return result;
    }
}
