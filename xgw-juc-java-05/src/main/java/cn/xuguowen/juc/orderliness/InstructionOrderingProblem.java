package cn.xuguowen.juc.orderliness;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

/**
 * ClassName: InstructionOrderingProblem
 * Package: cn.xuguowen.juc.orderliness
 * Description:指令重排序在多线程环境下的问题。这个代码在另一个maven的项目中运行：jcstress_ordering
 * 解决方法：volatile 修饰的变量，可以禁用指令重排
 *
 * @Author 徐国文
 * @Create 2024/6/25 11:52
 * @Version 1.0
 */
@JCStressTest
@Outcome(id = "0", expect = Expect.ACCEPTABLE_INTERESTING, desc = "i is 0, meaning reordering occurred.")
@Outcome(id = {"1", "4"}, expect = Expect.ACCEPTABLE, desc = "i is 1 or 4, meaning no reordering.")
@State
public class InstructionOrderingProblem {

    /**
     * 多线程环境下：
     * 在下面的代码中，如果没有适当的同步机制，指令重排可能导致 actor1 方法中的 r1 值为 0。
     * 这是因为 actor2 方法中的指令 1 和 2 可能被重排，导致 reday 被设置为 true 之前 num 还没有被设置为 2。
     */

    int num = 0;

    boolean reday = false;

    // 线程1 执行此方法
    @Actor
    public void actor1(I_Result r) {
        if(reday) {
            r.r1 = num + num;
        } else {
            r.r1 = 1;
        }
    }
    // 线程2 执行此方法
    @Actor
    public void actor2(I_Result r) {
        num = 2;
        reday = true;
    }

}
