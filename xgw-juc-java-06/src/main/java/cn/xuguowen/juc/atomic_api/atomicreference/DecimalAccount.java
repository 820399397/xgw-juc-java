package cn.xuguowen.juc.atomic_api.atomicreference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: DecimalAccount
 * Package: cn.xuguowen.juc.cas_volatile
 * Description:使用原子引用AtomicReference来优化代码
 *
 * @Author 徐国文
 * @Create 2024/7/3 12:34
 * @Version 1.0
 */
public interface DecimalAccount {
    // 获取余额
    BigDecimal getBalance();

    // 取款
    void withdraw(BigDecimal amount);

    /**
     * 方法内会启动 1000 个线程，每个线程做 -10 元 的操作
     * 如果初始余额为 10000 那么正确的结果应当是 0
     */
    static void demo(DecimalAccount account) {
        List<Thread> ts = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(BigDecimal.TEN);
            }));
        }
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(account.getBalance()
                + " cost: " + (end-start)/1000_000 + " ms");
    }
}
