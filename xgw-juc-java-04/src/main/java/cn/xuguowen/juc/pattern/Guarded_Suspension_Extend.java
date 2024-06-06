package cn.xuguowen.juc.pattern;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * ClassName: Guarded_Suspension_Extend
 * Package: cn.xuguowen.juc.pattern
 * Description:对同步模式之保护性暂停进行扩展
 * 如果需要在多个类之间使用 GuardedObject 对象，作为参数传递不是很方便，因此设计一个用来解耦的中间类，
 * 这样不仅能够解耦【结果等待者】和【结果生产者】，还能够同时支持多个任务的管理.
 * RPC框架中经常用到这样的设计模式。我们可以模拟一个demo的。@See cn.xuguowen.juc.pattern.RPC_Demo
 *
 * @Author 徐国文
 * @Create 2024/6/6 13:05
 * @Version 1.0
 */
@Slf4j(topic = "c.Guarded_Suspension_Extend")
public class Guarded_Suspension_Extend {

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new People().start();
        }

        Sleeper.sleep(1);

        for (Integer id : MailBoxes.getIds()) {
            new Postman(id, "内容" + id).start();
        }
    }
}

// 居民楼里的居民
@Slf4j(topic = "c.People")
class People extends Thread{
    @Override
    public void run() {
        // 居民就是从楼下的邮件箱中收信了
        GuardedSuspension4 guardedObject = MailBoxes.createGuardedObject();
        log.debug("开始收信,id:{}",guardedObject.getId());
        Object mail = guardedObject.getResponse(4000);
        log.debug("收到信 id:{}, 内容:{}", guardedObject.getId(), mail);
    }
}

// 邮寄员
@Slf4j(topic = "c.Postman")
class Postman extends Thread {
    // 信件ID
    private int id;
    // 信件内容
    private String mail;

    public Postman(int id, String mail) {
        this.id = id;
        this.mail = mail;
    }

    @Override
    public void run() {
        // 邮寄员就是把信送到楼下的邮件箱里了
        GuardedSuspension4 guardedSuspension = MailBoxes.getGuardedSuspension(id);
        log.debug("送信 id:{}, 内容:{}", id, mail);
        guardedSuspension.complete(mail);
    }
}

class MailBoxes {
    // 将来这个boxes是要被多个线程所共享的，所以要使用线程安全的map。目前阶段学习过线程安全的map就是Hashtable了
    private static final Map<Integer, GuardedSuspension4> boxes = new Hashtable<>();

    // 消息唯一标识
    public static int id = 1;

    // 顺序递增消息唯一标识:注意线程安全问题
    public static synchronized int generateId() {
        return id++;
    }

    // 如下的方法为什么没有加synchronized呢？是因为boxes本身就是线程安全的集合。也没有涉及到方法的组合使用。

    // 创建信封，也就是创建结果
    public static GuardedSuspension4 createGuardedObject() {
        GuardedSuspension4 guardedSuspension4 = new GuardedSuspension4(generateId());
        boxes.put(guardedSuspension4.getId(), guardedSuspension4);
        return guardedSuspension4;
    }

    // 根据消息ID获取消息结果
    public static GuardedSuspension4 getGuardedSuspension(int id) {
        // 不要用get，要用remove。二者的区别就是get不会删除集合中的元素。而remove会返回被删除的元素。因为这是boxes是一个静态变量，不能一直存不删除的。
        // return boxes.get(id);
        return boxes.remove(id);
    }

    // 获取所有的消息唯一标识
    public static Set<Integer> getIds() {
        return boxes.keySet();
    }
}


@Slf4j(topic = "c.GuardedSuspension4")
class GuardedSuspension4 {

    // 结果的唯一标识
    private Integer id;
    // 结果
    private Object response;

    public GuardedSuspension4(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    // 获取结果：对如下获取结果代码的优化
    public Object getResponseImprove(long timeout) {
        synchronized (this) {
            // 等待的开始时间 15:00:00
            long begin = System.currentTimeMillis();

            // 要经历的时间
            long passedTime = 0;

            while (response == null) {
                // 其实 timeout - passedTime 就是我还要等待的时间
                long waitTime = timeout - passedTime;
                log.debug("waitTime: {}", waitTime);
                if (waitTime <= 0) {
                    log.debug("break...");
                    break;
                }
                try {
                    // 假设 timeout = 2000 也就是2s
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 计算出经历的时间 15:00:01,这种情况下就是提前被唤醒了
                passedTime = System.currentTimeMillis() - begin;
                log.debug("timePassed: {}, object is null {}", passedTime, response == null);
            }
            return response;
        }
    }

    // 获取结果
    public Object getResponse(long timeout) {
        synchronized (this) {
            // 等待的开始时间 15:00:00
            long begin = System.currentTimeMillis();

            // 经历的时间
            long passedTime = 0;

            while (response == null) {
                if (passedTime >= timeout) {
                    break;
                }
                try {
                    // 假设 timeout = 2000 也就是2s
                    this.wait(timeout - passedTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 计算出经历的时间 15:00:01
                passedTime = System.currentTimeMillis() - begin;
            }
            return response;
        }
    }

    // 生成结果
    public void complete(Object response) {
        synchronized (this) {
            this.response = response;
            this.notifyAll();
        }
    }
}