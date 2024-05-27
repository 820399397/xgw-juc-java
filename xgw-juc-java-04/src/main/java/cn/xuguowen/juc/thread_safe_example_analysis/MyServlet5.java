package cn.xuguowen.juc.thread_safe_example_analysis;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ClassName: MyServlet5
 * Package: cn.xuguowen.juc.thread_safe_example_analysis
 * Description:分析如下代码中的成员变量是否是线程安全的。
 *
 * @Author 徐国文
 * @Create 2024/5/27 12:55
 * @Version 1.0
 */
public class MyServlet5 extends HttpServlet {
    // 是否安全
    private UserService3 userService = new UserServiceImpl3();

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            userService.update();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

interface UserService3 {
    void update() throws SQLException;
}

class UserServiceImpl3 implements UserService3 {
    public void update() throws SQLException {
        // 是否安全
        // 线程安全。作为局部变量出现，不再是多个线程共享的了。所以其内部的Connection对象也不是多个线程共享的了
        UserDao2 userDao = new UserDaoImpl2();
        userDao.update();
    }
}

interface UserDao2 {
    void update() throws SQLException;
}

class UserDaoImpl2 implements UserDao2 {
    // 是否安全
    private Connection conn = null;

    public void update() throws SQLException {
        String sql = "update user set password = ? where username = ?";
        conn = DriverManager.getConnection("", "", "");
        // ...
        conn.close();
    }
}
