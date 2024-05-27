package cn.xuguowen.juc.thread_safe_example_analysis;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * ClassName: MyServlet3
 * Package: cn.xuguowen.juc.thread_safe_example_analysis
 * Description:分析如下代码中的成员变量是否是线程安全的。
 *
 * @Author 徐国文
 * @Create 2024/5/27 12:48
 * @Version 1.0
 */
public class MyServlet3 extends HttpServlet {

    // 是否安全
    // 由于 HttpServlet 是单例的，userService1 实例在多个线程之间共享。
    private UserService1 userService1 = new UserServiceImpl1();

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        userService1.update();
    }
}

interface UserService1 {

    void update();
}

class UserServiceImpl1 implements UserService1 {
    // 是否安全
    // 同样，UserServiceImpl1 的实例在多个线程之间共享，因此 userDao 也是共享的。
    // 由于 UserServiceImpl1 没有使用任何共享的可变状态（如成员变量）进行并发修改，因此是线程安全的。
    private UserDao userDao = new UserDaoImpl();

    public void update() {
        userDao.update();
    }
}

interface UserDao {
    void update();
}

class UserDaoImpl implements UserDao {
    @Override
    public void update() {
        String sql = "update user set password = ? where username = ?";
        // 是否安全
        // update() 方法中的数据库连接（Connection）是每次调用方法时创建的，是线程安全的。
        try (Connection conn = DriverManager.getConnection("","","")){
            // ...
        } catch (Exception e) {
            // ...
        }
    }
}