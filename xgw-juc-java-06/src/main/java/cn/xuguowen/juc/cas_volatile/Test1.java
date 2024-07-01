package cn.xuguowen.juc.cas_volatile;

/**
 * ClassName: Test1
 * Package: cn.xuguowen.juc.cas_valatile
 * Description:问题提出：有如下需求，保证 account.withdraw 取款方法的线程安全
 * 由不安全的实现方式==>线程安全的悲观锁方式==>线程安全的乐观锁方式
 *
 * @Author 徐国文
 * @Create 2024/6/30 11:10
 * @Version 1.0
 */
public class Test1 {

    public static void main(String[] args) {
        // 线程不安全的实现
        testUnsafe();

        // 线程安全：悲观锁的实现方式
        testSafeSynchronized();

        // 线程安全：乐观锁的实现方式
        testSafeCas();
    }

    private static void testSafeCas() {
        Account accountSafeCas = new AccountSafeCas(10000);
        Account.demo(accountSafeCas);
    }

    private static void testSafeSynchronized() {
        Account accountSafeSynchronized = new AccountSafeSynchronized(10000);
        Account.demo(accountSafeSynchronized);
    }

    /**
     * 线程不安全的实现
     */
    private static void testUnsafe() {
        Account accountUnsafe = new AccountUnsafe(10000);
        Account.demo(accountUnsafe);
    }

}
