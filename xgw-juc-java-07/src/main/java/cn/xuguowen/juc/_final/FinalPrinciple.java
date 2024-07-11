package cn.xuguowen.juc._final;

/**
 * ClassName: FinalPrinciple
 * Package: cn.xuguowen.juc._final
 * Description:final关键字的原理：理解了 volatile 原理，再对比 final 的实现就比较简单了
 *
 * @Author 徐国文
 * @Create 2024/7/11 16:27
 * @Version 1.0
 */
public class FinalPrinciple {
    // 使用如下的命令获取当前类的字节码指令：javap -c FinalPrinciple
    /*
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: aload_0
       5: bipush        20
       7: putfield      #2                  // Field a:I
           => 加入写屏障的。保证可见性和防止指令重排
      10: return

     */


    final int a = 20;
}
