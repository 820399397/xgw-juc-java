package cn.xuguowen.juc.pattern;

/**
 * ClassName: Sync_Alternateexecution
 * Package: cn.xuguowen.juc.pattern
 * Description:同步模式之交替执行。
 * 题目要求：线程 1 输出 a 5 次，线程 2 输出 b 5 次，线程 3 输出 c 5 次。现在要求输出 abcabcabcabcabc 怎么实现.
 * 当前代码是使用的wait/notifyAll实现的。
 *
 * @Author 徐国文
 * @Create 2024/6/13 12:27
 * @Version 1.0
 */
public class Sync_Alternateexecution1 {

    public static void main(String[] args) {
        PrintAlternately printAlternately = new PrintAlternately(1, 5);

        // new Thread(() -> printAlternately.print("a", 1, 2)).start();
        // new Thread(() -> printAlternately.print("b", 2, 3)).start();
        // new Thread(() -> printAlternately.print("c", 3, 1)).start();


        new Thread(() -> printAlternately.print1("a", 1, 2)).start();
        new Thread(() -> printAlternately.print1("b", 2, 3)).start();
        new Thread(() -> printAlternately.print1("c", 3, 1)).start();
    }
}

/*
输出：abcabcabcabcabc
内容      等待标记        下一个标记
 a          1               2
 b          2               3
 c          3               1
 */
class PrintAlternately {

    // 等待标记。
    // 例如：当等待标记是 1 的时候，线程 1 输出 a
    private Integer flag;

    // 循环次数，当前题目是要求5次
    private Integer loopNumber = 5;

    public PrintAlternately(Integer flag, Integer loopNumber) {
        this.flag = flag;
        this.loopNumber = loopNumber;
    }

    /**
     * 打印
     *
     * @param content  当前线程打印的内容
     * @param waitFlag 当前线程打印的条件
     * @param nextFlag 当前线程打印之后通知下一个线程打印的条件
     */
    public void print(String content, Integer waitFlag, Integer nextFlag) {
        for (int i = 0; i < this.loopNumber; i++) {
            synchronized (this) {
                while (this.flag != waitFlag) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.print(content);
                this.flag = nextFlag;
                this.notifyAll();
            }
        }
    }

    /**
     * 打印1:
     * 如果将for循环放置到synchronized代码块内，会是什么样的结果呢？
     *
     * @param content  当前线程打印的内容
     * @param waitFlag 当前线程打印的条件
     * @param nextFlag 当前线程打印之后通知下一个线程打印的条件
     */
    public void print1(String content, Integer waitFlag, Integer nextFlag) {
        synchronized (this) {
            for (int i = 0; i < this.loopNumber; i++) {
                while (this.flag != waitFlag) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.print(content);
                this.flag = nextFlag;
                this.notifyAll();
            }
        }
    }
}
