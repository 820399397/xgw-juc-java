package cn.xuguowen.juc.pattern;

import cn.xuguowen.juc.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: RPC_Demo
 * Package: cn.xuguowen.juc.pattern
 * Description:在RPC框架中，确实使用了类似的设计模式来解耦请求者和响应者，通过中间管理器来管理异步请求和响应的匹配。
 * 以下是一个简化的例子，展示了如何在RPC框架中使用类似的设计模式进行请求和响应的处理。
 * RPC框架示例：
 * 1.中间类：RequestResponseManager：管理请求和响应之间的映射关系。
 * 2.RPCRequest：封装请求信息，包括唯一的请求ID。
 * 3.RPCResponse：封装响应信息，包括请求ID。
 * 4.Client：发送请求并等待响应。
 * 5.Server：接收请求并发送响应。
 *
 * @Author 徐国文
 * @Create 2024/6/6 13:51
 * @Version 1.0
 */
public class RPC_Demo {

    /**
     * 主线程创建了一个 Client 实例，并启动了3个额外的线程，每个线程代表一个客户端，模拟发送请求。
     * 每个客户端线程发送请求后，会等待响应结果。同时，服务器端会接收请求并生成响应，然后通知等待的客户端线程。
     * 这种设计模式下，主线程负责启动客户端线程，每个客户端线程独立发送请求并等待响应，服务器端负责接收请求并生成响应，通过 GuardedObject 进行请求和响应的映射管理。
     * @param args
     */
    public static void main(String[] args) {
        RequestResponseManager manager = new RequestResponseManager();
        Server.setManager(manager);

        Client client = new Client(manager);

        // 模拟发送3个请求
        for (int i = 0; i < 3; i++) {
            String requestId = "request" + i;
            String requestData = "数据" + i;
            new Thread(() -> client.sendRequest(requestId, requestData)).start();
        }
    }
}

// 中间类：管理请求和响应之间的映射关系。
class RequestResponseManager {
    private final Map<String, GuardedObject> requests = new ConcurrentHashMap<>();


    // 创建新的GuardedObject并将其存储到请求映射中
    public GuardedObject createRequest(String requestId) {
        GuardedObject guardedObject = new GuardedObject(requestId);
        requests.put(requestId, guardedObject);
        return guardedObject;
    }

    // 根据请求ID获取并移除对应的GuardedObject
    public GuardedObject getRequest(String requestId) {
        return requests.remove(requestId);
    }
}

// GuardedObject类，用于等待和获取异步结果
class GuardedObject {
    private final String id;
    private Object response;

    public GuardedObject(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public synchronized Object getResponse(long timeout) {
        long begin = System.currentTimeMillis();
        long passedTime = 0;

        while (response == null) {
            long waitTime = timeout - passedTime;
            if (waitTime <= 0) {
                break;
            }
            try {
                this.wait(waitTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            passedTime = System.currentTimeMillis() - begin;
        }
        return response;
    }

    public synchronized void complete(Object response) {
        this.response = response;
        this.notifyAll();
    }
}

// RPCRequest：封装请求信息，包括唯一的请求ID。
class RPCRequest {
    private final String requestId;
    private final String requestData;

    public RPCRequest(String requestId, String requestData) {
        this.requestId = requestId;
        this.requestData = requestData;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getRequestData() {
        return requestData;
    }
}

// RPCResponse：封装响应信息，包括请求ID。
class RPCResponse {
    private final String requestId;
    private final String responseData;

    public RPCResponse(String requestId, String responseData) {
        this.requestId = requestId;
        this.responseData = responseData;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getResponseData() {
        return responseData;
    }
}


// 客户端
@Slf4j(topic = "c.Client")
class Client {
    private final RequestResponseManager manager;

    public Client(RequestResponseManager manager) {
        this.manager = manager;
    }

    public void sendRequest(String requestId, String data) {
        // 创建并存储GuardedObject
        GuardedObject guardedObject = manager.createRequest(requestId);
        RPCRequest request = new RPCRequest(requestId, data);
        log.info("发送请求: {}", request.getRequestData());

        // 模拟发送请求到服务器
        Server.receiveRequest(request);

        Object response = guardedObject.getResponse(5000);
        log.info("收到响应: {}", response);
    }
}

// 服务端
@Slf4j(topic = "c.Server")
class Server extends Thread{
    private static RequestResponseManager manager;

    // 设置RequestResponseManager实例
    public static void setManager(RequestResponseManager manager) {
        Server.manager = manager;
    }


    // 接收并处理请求
    public static void receiveRequest(RPCRequest request) {
        new Thread(() -> {
            log.info("服务器接收请求: {}", request.getRequestData());
            // 模拟处理请求
            RPCResponse response = new RPCResponse(request.getRequestId(), "响应数据 " + request.getRequestData());

            // 模拟延迟
            // Sleeper.sleep(6);

            // 模拟发送响应
            sendResponse(response);
        }).start();
    }

    // 发送响应并通知等待的线程
    public static void sendResponse(RPCResponse response) {

        GuardedObject guardedObject = manager.getRequest(response.getRequestId());
        if (guardedObject != null) {
            // guardedObject.complete(response.getResponseData());

            // 模拟虚假唤醒
            guardedObject.complete(null);
        } else {
            log.warn("没有找到对应请求的GuardedObject, 请求ID: {}", response.getRequestId());
        }
    }
}