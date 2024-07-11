package cn.xuguowen.juc._final;

/**
 * ClassName: FinalJVMInstruct
 * Package: cn.xuguowen.juc._final
 * Description:查看如下类的JVM指令。
 *
 * @Author 徐国文
 * @Create 2024/7/11 16:36
 * @Version 1.0
 */
public class FinalJVMInstruct {

    // UseFinal中对应的字节码指令是: BIPUSH 10.可以这样理解：从FinalJVMInstruct类中拷贝一份到自己的类中，直接从自己的类中加载。这个过程中不涉及到共享，所以说final修饰的变量天生具备线程安全的特征
    final static int A = 10;

    // UseFinal中对应的字节码指令是：LDC 32768。可以这样理解：对于较大的值是直接存储到自己的常量池中，直接从自己的常量池中获取的。
    final static int B = Short.MAX_VALUE + 1;

    // UseFinal中对应的字节码指令是：GETSTATIC cn/xuguowen/juc/_final/FinalJVMInstruct.C : I。发现是从FinalJVMInstruct类中获取的，涉及到共享问题
    static int C = 10;

    // UseFinal中对应的字节码指令是：GETSTATIC cn/xuguowen/juc/_final/FinalJVMInstruct.D : I。发现是从FinalJVMInstruct类中获取的，涉及到共享问题
    static int D = Short.MAX_VALUE + 1;

    final int a = 20;
    final int b = Integer.MAX_VALUE;

}

class UseFinal {
    public void test() {
        System.out.println(FinalJVMInstruct.A);
        System.out.println(FinalJVMInstruct.B);
        System.out.println(FinalJVMInstruct.C);
        System.out.println(FinalJVMInstruct.D);
        System.out.println(new FinalJVMInstruct().a);
        System.out.println(new FinalJVMInstruct().b);
    }
}
