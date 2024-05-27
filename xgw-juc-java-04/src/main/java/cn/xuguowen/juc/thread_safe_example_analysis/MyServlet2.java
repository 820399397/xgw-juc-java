package cn.xuguowen.juc.thread_safe_example_analysis;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName: MyServlet2
 * Package: cn.xuguowen.juc.thread_safe_example_analysis
 * Description:分析如下代码是否是线程安全的。
 *
 * @Author 徐国文
 * @Create 2024/5/27 12:39
 * @Version 1.0
 */
public class MyServlet2 extends HttpServlet {

    // 是否安全？
    /*
        HttpServlet 在 Tomcat 中是单例的。这意味着 Tomcat 创建一个 HttpServlet 实例来处理所有对该 Servlet 的请求，而不是为每个请求创建一个新的实例。
        这是为了提高性能和减少内存开销。但是，这种单例模式也带来了一些线程安全问题，因为多个线程会并发地访问同一个 Servlet 实例的成员变量。
        那这也的话，下面的userService实例对象也是被多个线程所共享的。然后其内部有一个count成员变量，并在update()方法中对其有读写操作，所以是线程不安全的
     */
    private UserService userService = new UserServiceImpl();

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        userService.update();
    }

}

interface UserService {

    void update();
}

class UserServiceImpl implements UserService {
    // 记录调用次数
    private int count = 0;

    public void update() {
        // ...
        count++;
    }
}
