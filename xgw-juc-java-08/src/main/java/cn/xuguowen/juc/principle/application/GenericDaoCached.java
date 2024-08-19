package cn.xuguowen.juc.principle.application;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ClassName: GenericDaoCached
 * Package: cn.xuguowen.juc.principle.application
 * Description:使用装饰器模式对GenericDao增加缓存功能
 * 目前使用缓存优化后存在的问题：
 * 1.缓存容器使用不合理。HashMap不是线程安全的
 * 2.queryOne()方法存在多个线程查询的问题。在第一次查询的时候，多个线程判断出缓存中没有数据，然后就都去查询数据库了，会造成一定的压力。
 * 3.update()方法存在数据不一致的问题：
 *  3.1先更新缓存的话。比如说B线程执行update方法的清除缓存操作。A线程执行了查询操作，查询出来的结果比如说是x=1，然后将x=1放入缓存中。此时B线程菜执行update操作，将x更新为x=2。然后A线程后续的查询操作，查询的还是缓存中的x=1。
 *  3.2先更新数据库：比如说B线程先将数据改为x=2。此时A线程查询，走的是缓存，查询出来的是x=1.此时B线程执行了缓存的清除操作。后续A线程的查询操作，发现缓存中没有数据，查询数据库，查询出来的值是x=2，是正确的。
 *              虽然两种做法都会存在一定的数据不一致问题。但是方式2造成数据不一致的问题是短暂的，后续可以自动的修正过来。
 * 3.3补充一种情况：假设查询线程 A 查询数据时恰好缓存数据由于时间到期失效，或是第一次查询。A线程查询数据，缓存没有，查询数据库x=1，还没来的及放入缓存中呢。此时B线程更新操作来了：将x=2更新到库里，然后清除缓存。
 *                  B线程做完之后，A线程将刚刚查询到的x=1放入缓存中，后续查询的结果也一直是x=1，这显然是不正确的。
 * <p>
 * 针对以上存在的3个问题：使用读写锁进行优化。。。
 * 以下实现体现的是读写锁的应用，保证缓存和数据库的一致性，但有下面的问题没有考虑：
 *  - 适合读多写少，如果写操作比较频繁，以下实现性能低
 *  - 没有考虑缓存容量
 *  - 没有考虑缓存过期
 *  - 只适合单机
 *  - 并发性还是低，目前只会用一把锁
 *  - 更新方法太过简单粗暴，清空了所有 key（考虑按类型分区或重新设计 key）
 *
 *
 * @Author 徐国文
 * @Create 2024/8/12 16:50
 * @Version 1.0
 */
public class GenericDaoCached extends GenericDao {

    // 装饰器模式增强基类
    private GenericDao genericDao = new GenericDao();

    // 缓存map
    private Map<SqlPair, Object> cacheMap = new HashMap<>();

    // 读写锁
    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();


    @Override
    public <T> List<T> queryList(Class<T> beanClass, String sql, Object... args) {
        return super.queryList(beanClass, sql, args);
    }

    /**
     * 增加缓存功能
     *
     * @param beanClass
     * @param sql
     * @param args
     * @param <T>
     * @return
     */
    @Override
    public <T> T queryOne(Class<T> beanClass, String sql, Object... args) {
        SqlPair sqlPair = new SqlPair(sql, args);
        // 加入读锁进行优化
        rw.readLock().lock();
        try {
            // 1.先从缓存中找
            T value = (T) cacheMap.get(sqlPair);
            if (Objects.nonNull(value)) {
                return value;
            }
        } finally {
            //  释放读锁
            rw.readLock().unlock();
        }

        // 加入写锁：将数据写入到缓存中
        rw.writeLock().lock();
        try {
            // 再次检查，防止其他线程已经将数据写入到缓存中
            T value = (T) cacheMap.get(sqlPair);
            if (Objects.nonNull(value)) {
                return value;
            }
            // 2.查询数据库
            value = super.queryOne(beanClass, sql, args);
            // 3.放入缓存
            cacheMap.put(sqlPair, value);
            return value;
        } finally {
            // 释放写锁
            rw.writeLock().unlock();
        }

    }

    @Override
    public int update(String sql, Object... args) {
        // 加入写锁
        rw.writeLock().unlock();
        try {
            // 1.先更新数据库
            int rows = super.update(sql, args);
            // 清除缓存
            cacheMap.clear();
            return rows;
        } finally {
            // 释放写锁写锁
            rw.writeLock().unlock();
        }
    }

    class SqlPair {
        private String sql;
        private Object[] args;

        public SqlPair(String sql, Object[] args) {
            this.sql = sql;
            this.args = args;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SqlPair sqlPair = (SqlPair) o;
            return Objects.equals(sql, sqlPair.sql) && Arrays.equals(args, sqlPair.args);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(sql);
            result = 31 * result + Arrays.hashCode(args);
            return result;
        }
    }
}
