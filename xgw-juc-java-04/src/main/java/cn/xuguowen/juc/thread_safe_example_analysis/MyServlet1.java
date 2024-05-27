package cn.xuguowen.juc.thread_safe_example_analysis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: MyServlet
 * Package: cn.xuguowen.juc.thread_safe_example_analysis
 * Description:分析如下代码中的成员变量是否是线程安全的。
 *
 * @Author 徐国文
 * @Create 2024/5/27 12:33
 * @Version 1.0
 */
public class MyServlet1 extends HttpServlet {

    // 是否安全？
    // HashMap 是非线程安全的集合，如果多个线程同时访问并修改它，会出现竞态条件（race condition），可能导致数据不一致、死锁等问题。
    // 使用线程安全的集合类，例如 ConcurrentHashMap。
    Map<String,Object> map = new HashMap<>();

    // 是否安全？
    // String 是不可变类（immutable class），一旦创建，其内容就无法修改，因此在多线程环境下可以安全地共享。
    String S1 = "...";

    // 是否安全？
    // 与 S1 一样，S2 是不可变类，且 final 确保引用不可改变。因此它也是线程安全的。
    final String S2 = "...";

    // 是否安全？
    // Date 是可变类（mutable class），它的内部状态可以改变。如果多个线程共享 D1 并对其进行修改，可能会出现数据不一致的问题。
    Date D1 = new Date();

    // 是否安全？
    // 虽然 D2 是 final 的，但这只意味着 D2 的引用不可改变，D2 指向的 Date 对象本身仍然是可变的。多个线程对 D2 进行修改依然会导致线程安全问题。
    final Date D2 = new Date();

    // 如果需要在多线程环境下使用日期对象，可以使用不可变的日期类，
    // 例如 java.time.LocalDate 或 java.time.LocalDateTime。或者，在需要共享 Date 对象时进行深度拷贝。



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 使用上述变量

        // 如果必须使用 Date，可以在每次访问时进行深拷贝。
        Date safeD1 = new Date(D1.getTime());
        Date safeD2 = new Date(D2.getTime());
    }
}
