package cn.xuguowen.juc.cas_volatile;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName: AccountSafeCas
 * Package: cn.xuguowen.juc.cas_valatile
 * Description:线程安全：乐观锁的实现。虽然叫乐观锁，其实是没有锁的。使用cas+volatile实现。
 * CAS 必须借助 volatile 才能读取到共享变量的最新值来实现【比较并交换】的效果
 *
 * 为什么无锁效率高?
 * CAS（Compare-And-Swap，比较并交换）无锁机制的效率高，主要是因为它避免了线程阻塞和上下文切换的开销。下面是对比CAS无锁和synchronized悲观锁的详细分析：
 * 悲观锁（synchronized）的工作原理和开销
 *  - 锁竞争：使用synchronized关键字时，当多个线程尝试进入同步块时，如果已经有线程持有该锁，其他线程将被阻塞，进入阻塞状态。
 *  - 上下文切换：当一个线程被阻塞时，操作系统会进行上下文切换，将CPU资源分配给其他线程。上下文切换是有开销的，因为需要保存和恢复线程的状态。
 *  - 锁的释放：持有锁的线程在离开同步块时会释放锁，其他被阻塞的线程才有机会重新竞争锁，继续执行。这个过程会有一定的延迟。
 * 这些开销在高并发环境下会显得尤为明显，尤其是当锁竞争激烈时，阻塞和上下文切换的频率会非常高，导致性能下降。
 * CAS无锁机制的工作原理和优势
 *  - 乐观锁：CAS是一种乐观锁机制，假设不会有冲突，因此直接进行更新操作。如果冲突发生（其他线程修改了值），则重试操作。
 *  - 避免阻塞：CAS通过原子操作进行比较并交换，不会导致线程阻塞。失败的线程会自旋重试，而不是进入阻塞状态，这样可以避免上下文切换的开销。
 *  - 硬件支持：CAS操作通常是由硬件指令支持的（如x86上的CMPXCHG指令），这些指令在硬件层面保证原子性，非常高效。
 *
 * CAS的实现和优缺点：
 *  优点：
 *      - 高性能：由于避免了线程阻塞和上下文切换，CAS操作在高并发环境下性能非常高。
 *      - 无锁并发：CAS允许多个线程在没有锁的情况下进行并发操作，提高了系统的吞吐量。
 *  缺点：
 *      - 自旋等待：如果多个线程频繁尝试CAS操作，会导致自旋等待，消耗CPU资源。不过在实践中，自旋等待的开销通常小于阻塞和上下文切换的开销。
 *      - ABA问题：CAS操作可能会遇到ABA问题，即一个值在比较期间被改为其他值又改回原值。可以使用版本号或标记来解决这个问题。
 *
 * CAS 的特点：结合 CAS 和 volatile 可以实现无锁并发，适用于线程数少、多核 CPU 的场景下。
 *  - CAS 是基于乐观锁的思想：最乐观的估计，不怕别的线程来修改共享变量，就算改了也没关系，我吃亏点再重试呗。
 *  - synchronized 是基于悲观锁的思想：最悲观的估计，得防着其它线程来修改共享变量，我上了锁你们都别想改，我改完了解开锁，你们才有机会。
 *  - CAS 体现的是无锁并发、无阻塞并发，请仔细体会这两句话的意思
 *      因为没有使用 synchronized，所以线程不会陷入阻塞，这是效率提升的因素之一
 *      但如果竞争激烈，可以想到重试必然频繁发生，反而效率会受影响
 *
 *
 * @Author 徐国文
 * @Create 2024/6/30 11:18
 * @Version 1.0
 */
public class AccountSafeCas implements Account{

    private AtomicInteger balance;

    public AccountSafeCas(Integer balance) {
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public Integer getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(Integer amount) {
        while (true) {
            //  获取当前余额
            int prev = balance.get();
            //  计算扣减后的余额
            int next = prev - amount;
            // cas操作 compareAndSet()的返回值是boolean类型的
            //  如果prev的值和当前balance进行比较，如果相等，则说明没有其他人修改，那就是我修改，修改完毕之后退出while循环
            //  如果prev的值和当前balance进行比较，如果不相等，则说明其他人修改过了，那我再次循环尝试
            if (balance.compareAndSet(prev, next)) {
                break;
            }
        }

        // 可以简化为如下写法：
        // balance.addAndGet(-1 * amount);
    }
}
