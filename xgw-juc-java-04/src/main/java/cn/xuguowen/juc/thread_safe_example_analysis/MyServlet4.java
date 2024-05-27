package cn.xuguowen.juc.thread_safe_example_analysis;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ClassName: MyServlet4
 * Package: cn.xuguowen.juc.thread_safe_example_analysis
 * Description:分析如下代码中的成员变量是否是线程安全的。
 *
 * @Author 徐国文
 * @Create 2024/5/27 12:52
 * @Version 1.0
 */
public class MyServlet4 extends HttpServlet {
    // 是否安全
    private UserService2 userService = new UserServiceImpl2();

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            userService.update();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

interface UserService2 {
    void update() throws SQLException;
}

class UserServiceImpl2 implements UserService2 {
    // 是否安全
    private UserDao1 userDao = new UserDaoImpl1();

    public void update() throws SQLException {
        userDao.update();
    }
}

interface UserDao1 {
    void update() throws SQLException;
}

class UserDaoImpl1 implements UserDao1{
    // 是否安全
    // UserDaoImpl1 中的 conn 是一个成员变量。由于 UserDaoImpl1 的实例在多个线程之间共享，因此 conn 也会被多个线程共享。
    // 在多线程环境中，如果一个线程在使用 conn 时，另一个线程可能会重新分配这个 conn，从而导致连接的并发使用问题。这会引起线程安全问题，如数据不一致、连接泄漏等。
    private Connection conn = null;
    public void update() throws SQLException {
        String sql = "update user set password = ? where username = ?";
        conn = DriverManager.getConnection("","","");
        // ...
        conn.close();
    }
}