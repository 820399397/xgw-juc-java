package cn.xuguowen.juc.cas_volatile;

/**
 * ClassName: AccountSafeSynchronized
 * Package: cn.xuguowen.juc.cas_valatile
 * Description:线程安全；悲观锁的实现方式
 *
 * @Author 徐国文
 * @Create 2024/6/30 11:16
 * @Version 1.0
 */
public class AccountSafeSynchronized implements Account{

    private Integer balance;

    public AccountSafeSynchronized(Integer balance) {
        this.balance = balance;
    }


    @Override
    public Integer getBalance() {
        synchronized (this) {
            return this.balance;
        }
    }

    @Override
    public void withdraw(Integer amount) {
        synchronized (this) {
            this.balance -= amount;
        }
    }
}
