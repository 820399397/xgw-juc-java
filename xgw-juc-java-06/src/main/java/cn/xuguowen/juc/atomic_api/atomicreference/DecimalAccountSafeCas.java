package cn.xuguowen.juc.atomic_api.atomicreference;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ClassName: DecimalAccountSafeCas
 * Package: cn.xuguowen.juc.atomic_api.atomicreference
 * Description:
 *
 * @Author 徐国文
 * @Create 2024/7/3 12:38
 * @Version 1.0
 */
public class DecimalAccountSafeCas implements DecimalAccount{

    private AtomicReference<BigDecimal> balance;

    public DecimalAccountSafeCas(BigDecimal balance) {
        this.balance = new AtomicReference<>(balance);
    }

    @Override
    public BigDecimal getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(BigDecimal amount) {
        while (true) {
            // 获取之前的balance
            BigDecimal prev = balance.get();
            // 计算下一个balance
            BigDecimal next = prev.subtract(amount);
            // 比较并设置balance
            if (balance.compareAndSet(prev,next)) {
                break;
            }
        }
    }
}
