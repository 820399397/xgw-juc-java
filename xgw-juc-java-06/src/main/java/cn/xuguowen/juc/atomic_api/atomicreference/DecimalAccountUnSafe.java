package cn.xuguowen.juc.atomic_api.atomicreference;

import java.math.BigDecimal;

/**
 * ClassName: DecimalAccountUnSafe
 * Package: cn.xuguowen.juc.atomic_api.atomicreference
 * Description:线程不安全的实现
 *
 * @Author 徐国文
 * @Create 2024/7/3 12:44
 * @Version 1.0
 */
public class DecimalAccountUnSafe implements DecimalAccount{

    private BigDecimal balance;

    public DecimalAccountUnSafe(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        BigDecimal prev = this.getBalance();
        this.balance = prev.subtract(amount);
    }
}
