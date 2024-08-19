package cn.xuguowen.juc.principle;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ClassName: CachedData
 * Package: cn.xuguowen.juc.principle
 * Description:JDK中ReentrantReadWriteLock的文档中针对于读写锁重入时降级支持提供了一个类加以说明，这个类就是CachedData。
 *
 * @Author 徐国文
 * @Create 2024/8/12 16:40
 * @Version 1.0
 */
public class CachedData {
    Object data;
    // 是否有效，如果失效，需要重新计算 data
    volatile boolean cacheValid;
    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    void processCachedData() {
        rwl.readLock().lock();
        if (!cacheValid) {
            // 获取写锁前必须释放读锁
            rwl.readLock().unlock();
            rwl.writeLock().lock();
            try {
                // 判断是否有其它线程已经获取了写锁、更新了缓存, 避免重复更新
                if (!cacheValid) {
                    data = 1;
                    cacheValid = true;
                }
                // 降级为读锁, 释放写锁, 这样能够让其它线程读取缓存
                rwl.readLock().lock();
            } finally {
                rwl.writeLock().unlock();
            }
        }
        // 自己用完数据, 释放读锁
        try {
            use(data);
        } finally {
            rwl.readLock().unlock();
        }
    }

    private void use(Object data) {
    }
}
