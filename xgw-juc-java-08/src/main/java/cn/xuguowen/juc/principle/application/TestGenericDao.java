package cn.xuguowen.juc.principle.application;

/**
 * ClassName: TestGenericDao
 * Package: cn.xuguowen.juc.principle.application
 * Description:
 *
 * @Author 徐国文
 * @Create 2024/8/12 16:59
 * @Version 1.0
 */
public class TestGenericDao {
    public static void main(String[] args) {
        GenericDao dao = new GenericDaoCached();
        System.out.println("============> 查询");
        String sql = "select * from user where id = ?";
        int id = 1;
        User user = dao.queryOne(User.class, sql, id);
        System.out.println(user);
        user = dao.queryOne(User.class, sql, id);
        System.out.println(user);
        user = dao.queryOne(User.class, sql, id);
        System.out.println(user);

        System.out.println("============> 更新");
        dao.update("update user set user_nickname = ? where id = ?", "xgw", id);
        user = dao.queryOne(User.class, sql, id);
        System.out.println(user);
    }
}
