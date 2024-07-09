package cn.xuguowen.juc.unsafe;

import cn.xuguowen.juc.cas_volatile.Account;

/**
 * ClassName: TestAccountDemo
 * Package: cn.xuguowen.juc.unsafe
 * Description:如下需求，保证 account.withdraw 取款方法的线程安全.
 * 使用自定义的原子整数类保证了线程安全，基于unsafe实现的。
 *
 * @Author 徐国文
 * @Create 2024/7/9 14:21
 * @Version 1.0
 */
public class TestAccountDemo {
    public static void main(String[] args) {
        Account.demo(new AccountImpl(new MyActomicInteger(10000)));
    }
}

class AccountImpl implements Account {

    private MyActomicInteger myActomicInteger;

    public AccountImpl(MyActomicInteger myActomicInteger) {
        this.myActomicInteger = myActomicInteger;
    }

    @Override
    public Integer getBalance() {
        return myActomicInteger.getValue();
    }

    @Override
    public void withdraw(Integer amount) {
        myActomicInteger.decrease(amount);
    }
}
