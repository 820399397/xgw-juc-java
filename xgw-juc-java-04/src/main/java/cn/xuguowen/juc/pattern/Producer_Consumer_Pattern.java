package cn.xuguowen.juc.pattern;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * ClassName: Producer_Consumer_Pattern
 * Package: cn.xuguowen.juc.pattern
 * Description:异步模式之生产者/消费者
 * 要点：
 * 1.与前面的保护性暂停中的 GuardObject 不同，不需要产生结果和消费结果的线程一一对应
 * 2.消费队列可以用来平衡生产和消费的线程资源。前面的案例中1个结果等待线程就对应1个结果生产线程
 * 3.生产者仅负责产生结果数据，不关心数据该如何处理，而消费者专心处理结果数据
 * 4.消息队列是有容量限制的，满时不会再加入数据，空时不会再消耗数据
 * 5.JDK 中各种阻塞队列，采用的就是这种模式
 * 6.当前的消息队列不同于我们常见的MQ产品。我们是线程间的队列。
 *
 * @Author 徐国文
 * @Create 2024/6/6 22:20
 * @Version 1.0
 */
@Slf4j(topic = "c.Producer_Consumer_Pattern")
public class Producer_Consumer_Pattern {
    /**
     * 这段代码实现了经典的生产者-消费者模式，其中包含了生产者、消费者和消息队列三个关键组件。
     * @param args
     */
    public static void main(String[] args) {
        // 创建消息队列对象，指定队列容器的最大容量
        MessageQueue messageQueue = new MessageQueue(2);

        // 模拟3个生产者线程：生成消息
        for (int i = 0; i < 3; i++) {
            // 在lambda表达式中，不允许使用可变的变量。所以才有了如下的代码。每次循环，id都是一个新的值
            int id = i;
            new Thread(() -> {
                // 随机生成消息
                Message message = new Message(id, "值" + id);
                // 放入消息队列
                messageQueue.put(message);
            }, "生产者" + i).start();
        }

        // 模拟1个消费者线程，每隔1s消费消息
        new Thread(() -> {
            while (true){
                Sleeper.sleep(1);
                Message message = messageQueue.take();
            }
        },"消费者").start();

    }
}

// MessageQueue 类是消息队列的实现，使用 LinkedList 作为底层容器，以存储消息。它包含了 take() 和 put() 方法，分别用于消费者取出消息和生产者放入消息。这两个方法都被 synchronized 关键字修饰，以确保线程安全。
@Slf4j(topic = "c.MessageQueue")
class MessageQueue {
    // 容器：存放消息
    private final LinkedList<Message> queue = new LinkedList<>();

    // 容器的容量：消息队列满了则不允许放了
    private final int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    // 获取消息 如果队列为空，则消费者线程会进入等待状态，直到有新的消息被放入队列。当有新消息放入队列时，会通知所有等待的消费者线程继续执行。
    public Message take() {
        synchronized (queue) {
            while (queue.isEmpty()) {
                log.debug("消息队列为空，消费者等待生产者生产消息之后继续消费");
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Message message = queue.removeFirst();
            log.debug("消费者获取消息 {}", message);

            // 当队列的容量已满，然后消费者消费了消息之后，此时队列不在满了，则通知生产者继续生产消息
            queue.notifyAll();
            return message;
        }
    }


    // 存入消息 如果队列已满，则生产者线程会进入等待状态，直到队列有空位可以放入新的消息。当消费者线程消费了消息后，会通知所有等待的生产者线程继续执行。
    public void put(Message message) {
        synchronized (queue) {
            // 当队列的容量已经满了，则不允许继续放了
            while (queue.size() == capacity) {
                try {
                    log.debug("消息队列已满，生产者等待消息消费之后继续生产");
                    queue.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            queue.addLast(message);
            log.debug("生产者生产消息 {}", message);


            // 队列没有满，则通知消费者继续消费消息
            queue.notifyAll();
        }
    }

}

// Message 类表示消息实体，包含消息的 ID 和内容。它是不可变类，提供了 getId() 和 getMessage() 方法用于获取消息的属性。
@Slf4j(topic = "c.Message")
final class Message {

    /**
     * 消息实体中的ID字段主要用于标识消息的唯一性或顺序性。虽然在这个简单的例子中，并没有明确地使用ID字段来实现特定的功能，但是在实际的应用中，ID字段可以具有多种作用，例如：
     * 唯一标识: 每条消息可以通过其ID来唯一地标识。这对于跟踪消息、识别重复消息或进行消息去重等操作是很有用的。
     *
     * 排序: 消息可以按照ID的顺序进行排序。这对于需要按照特定顺序处理消息的场景是有帮助的，例如消息的先后顺序对任务的执行顺序有要求时。
     *
     * 路由: ID字段可以用于将消息路由到特定的处理逻辑或者分区。在分布式系统中，可以根据消息的ID来确定消息应该由哪个节点或者处理器来处理。
     *
     * 关联性: 如果消息之间存在一定的关联关系，那么ID字段可以用于表示这种关系。例如，某些消息可能是对其他消息的回复或者相关联的事件。
     */
    private Integer id;

    private Object message;

    public Message(Integer id, Object message) {
        this.id = id;
        this.message = message;
    }

    public Message() {
    }

    public Integer getId() {
        return id;
    }

    public Object getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message=" + message +
                '}';
    }
}
