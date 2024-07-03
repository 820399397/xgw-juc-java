package cn.xuguowen.juc.atomic_api.atomicreference;

import java.math.BigDecimal;

/**
 * ClassName: DecimalAccountSynchronized
 * Package: cn.xuguowen.juc.atomic_api.atomicreference
 * Description:线程安全的使用：使用悲观锁synchronized
 *
 * @Author 徐国文
 * @Create 2024/7/3 12:47
 * @Version 1.0
 */
public class DecimalAccountSynchronized implements DecimalAccount {

    private BigDecimal balance;

    public DecimalAccountSynchronized(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public BigDecimal getBalance() {
        synchronized (this) {
            return balance;
        }
    }

    @Override
    public void withdraw(BigDecimal amount) {
        synchronized (this) {
            BigDecimal prev = this.getBalance();
            this.balance = prev.subtract(amount);
        }
    }
}
