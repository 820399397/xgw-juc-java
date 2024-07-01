package cn.xuguowen.juc.cas_volatile;

/**
 * ClassName: AccountUnsafe
 * Package: cn.xuguowen.juc.cas_valatile
 * Description:线程不安全的实现
 *
 * @Author 徐国文
 * @Create 2024/6/30 11:11
 * @Version 1.0
 */
public class AccountUnsafe implements Account{

    private Integer balance;

    public AccountUnsafe(Integer balance) {
        this.balance = balance;
    }


    @Override
    public Integer getBalance() {
        return this.balance;
    }

    @Override
    public void withdraw(Integer amount) {
        this.balance -= amount;
    }
}
