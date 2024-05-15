package cn.xuguowen.juc;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.FutureTask;

/**
 * ClassName: ThreadApplication2_ImporveEffciency
 * Package: cn.xuguowen.juc
 * Description:线程应用之提升效率。
 * 1.充分利用多核 cpu 的优势，提高运行效率。想象下面的场景，执行 3 个计算，最后将计算结果汇总。
 *      计算 1 花费 10 ms
 *      计算 2 花费 11 ms
 *      计算 3 花费 9 ms
 *      汇总需要 1 ms
 *  如果是串行执行，那么总共花费的时间是 10 + 11 + 9 + 1 = 31ms
 *  但如果是四核 cpu，各个核心分别使用线程 1 执行计算 1，线程 2 执行计算 2，线程 3 执行计算 3，那么 3 个线程是并行的，花费时间只取决于最长的那个线程运行的时间，即 11ms 最后加上汇总时间只会花费 12ms
 *  注意：需要在多核 cpu 才能提高效率，单核仍然时是轮流执行
 * 2.设计代码如下：
 * 3.代码运行结果：
 *      Benchmark                                         Mode  Cnt  Score   Error  Units
 *      ThreadApplication2_ImporveEffciency.multiThread   avgt    5  0.011 ± 0.001   s/op
 *      ThreadApplication2_ImporveEffciency.singleThread  avgt    5  0.032 ± 0.001   s/op
 * 4.结论：
 *   ① 单核 cpu 下，多线程不能实际提高程序运行效率，只是为了能够在不同的任务之间切换，不同线程轮流使用cpu ，不至于一个线程总占用 cpu，别的线程没法干活
 *      如果当前案例想要测试单核CPU的环境下执行程序，可以借助虚拟机来实现。给虚拟机分配1个CPU核心数，然后打包项目到linux系统上运行
 *   ② 多核 cpu 可以并行跑多个线程，但能否提高程序运行效率还是要分情况的
 *      有些任务，经过精心设计，将任务拆分，并行执行，当然可以提高程序的运行效率。但不是所有计算任务都能拆分（参考后文的【阿姆达尔定律】）
 *      也不是所有任务都需要拆分，任务的目的如果不同，谈拆分和效率没啥意义。
 *   ③  IO 操作不占用 cpu，只是我们一般拷贝文件使用的是【阻塞 IO】，这时相当于线程虽然不用 cpu，但需要一直等待 IO 结束，没能充分利用线程。所以才有后面的【非阻塞 IO】和【异步 IO】优化
 * 5.使用场景：比如说一些后台管理系统的首页会有很多指标的统计，一般情况下指标的统计是要去连表查询的，那么对于每一个指标的统计就可以使用多线程去优化。
 *            线程A统计客户跟踪
 *            线程B统计客户满意度
 *            主线程等待所有线程统计完毕后，汇总结果，然后返回。
 *
 * @Author 徐国文
 * @Create 2024/5/15 13:10
 * @Version 1.0
 */
// 基准测试工具选择，使用了比较靠谱的 JMH(需要引入maven依赖信息)，它会执行程序预热(目的就是为了让JVM识别出热点代码，进行JIT即时编译优化)，执行多次测试并平均
@Fork(1)    // 这个注解指定了运行基准测试时启动的 Java 虚拟机（JVM）数量。
@BenchmarkMode(Mode.AverageTime)    // 这个注解指定了基准测试的模式，这里使用的是平均时间模式（AverageTime）。
@Warmup(iterations=3)       //  这个注解指定了预热的迭代次数。预热迭代是在真正的基准测试之前运行的迭代，用于使 JVM 达到稳定状态。
@Measurement(iterations=5)  //  这个注解指定了实际基准测试的迭代次数。
public class ThreadApplication2_ImporveEffciency {


    static int[] ARRAY = new int[1000_000_00];

    // 初始化数组，将数组中的所有元素都设置为1
    static {
        Arrays.fill(ARRAY, 1);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ThreadApplication2_ImporveEffciency.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }


    @State(Scope.Benchmark)
    public static class BenchmarkState {
        int[] array = ARRAY;
    }

    // @Benchmark:它用于标记一个方法作为基准测试方法。当你使用 @Benchmark 注解标记一个方法时，JMH 将会执行该方法，并测量其性能指标，比如执行时间等
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3)
    @Measurement(iterations = 5)
    @Fork(1)
    public static int multiThread(BenchmarkState state) throws Exception {
        int[] array = state.array;
        FutureTask<Integer> t1 = new FutureTask<>(()->{
            int sum = 0;
            for(int i = 0; i < 250_000_00;i++) {
                sum += array[0+i];
            }
            return sum;
        });
        FutureTask<Integer> t2 = new FutureTask<>(()->{
            int sum = 0;
            for(int i = 0; i < 250_000_00;i++) {
                sum += array[250_000_00+i];
            }
            return sum;
        });
        FutureTask<Integer> t3 = new FutureTask<>(()->{
            int sum = 0;
            for(int i = 0; i < 250_000_00;i++) {
                sum += array[500_000_00+i];
            }
            return sum;
        });
        FutureTask<Integer> t4 = new FutureTask<>(()->{
            int sum = 0;
            for(int i = 0; i < 250_000_00;i++) {
                sum += array[750_000_00+i];
            }
            return sum;
        });
        new Thread(t1).start();
        new Thread(t2).start();
        new Thread(t3).start();
        new Thread(t4).start();
        return t1.get() + t2.get() + t3.get()+ t4.get();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3)
    @Measurement(iterations = 5)
    @Fork(1)
    public static int singleThread(BenchmarkState state) throws Exception {
        int[] array = state.array;
        FutureTask<Integer> t1 = new FutureTask<>(()->{
            int sum = 0;
            for(int i = 0; i < 1000_000_00;i++) {
                sum += array[0+i];
            }
            return sum;
        });
        new Thread(t1).start();
        return t1.get();
    }

}
