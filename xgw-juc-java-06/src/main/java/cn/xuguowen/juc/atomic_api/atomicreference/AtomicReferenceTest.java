package cn.xuguowen.juc.atomic_api.atomicreference;

import java.math.BigDecimal;

/**
 * ClassName: AtomicReferenceTest
 * Package: cn.xuguowen.juc.atomic_api.atomicreference
 * Description:
 *
 * @Author 徐国文
 * @Create 2024/7/3 12:41
 * @Version 1.0
 */
public class AtomicReferenceTest {

    public static void main(String[] args) {
        // 线程不安全的实现
        DecimalAccountUnSafe decimalAccountUnSafe = new DecimalAccountUnSafe(new BigDecimal("10000"));
        DecimalAccount.demo(decimalAccountUnSafe);

        // 线程安全的实现，使用synchronized
        DecimalAccountSynchronized decimalAccountSynchronized = new DecimalAccountSynchronized(new BigDecimal("10000"));
        DecimalAccount.demo(decimalAccountSynchronized);

        // 线程安全的实现，使用cas
        DecimalAccountSafeCas accountSafeCas = new DecimalAccountSafeCas(new BigDecimal("10000"));
        DecimalAccount.demo(accountSafeCas);
    }
}
