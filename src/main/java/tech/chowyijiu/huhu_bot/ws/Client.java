package tech.chowyijiu.huhu_bot.ws;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.constant.GocqActionEnum;
import tech.chowyijiu.huhu_bot.constant.MetaTypeEnum;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.request.Params;
import tech.chowyijiu.huhu_bot.entity.gocq.request.RequestBox;
import tech.chowyijiu.huhu_bot.entity.gocq.response.MessageResp;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

/**
 * @author elastic chow
 * @date 13/5/2023
 */

@Slf4j
//@ClientEndpoint
//ws正向连接 客户端
@Deprecated
public class Client {

    private Session session;
    private static Client INSTANCE;

    private Client(String url) throws DeploymentException, IOException {
        session = ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create(url));
    }

    public synchronized static boolean connect(String url) {
        try {
            INSTANCE = new Client(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static class GocqRequest {
        public static void sendMessage(GocqActionEnum action, Params params) {
            RequestBox<Params> req = new RequestBox<>();
            req.setAction(action.getAction()).setParams(params);
            String json = JSONObject.toJSONString(req);
            Client.INSTANCE.session.getAsyncRemote().sendText(json);
            log.info("send message successfully, message: " + json);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("连接成功");
    }

    @OnMessage
    public void onMessage(String json) {
        log.info("accepted message, message: {}", json);
        try {
            MessageResp message = JSONObject.parseObject(json, MessageResp.class);
            //if ("message".equals(message.getPostType())) {
            //    sendMessage(new Params().setMessage(message.getMessage()).setUserId(message.getUserId()));
            //}
            log.info("json -> object: {}", message);
            if(PostTypeEnum.meta_event.toString().equals(message.getPostType()) && MetaTypeEnum.heartbeat.toString().equals(message.getMetaEventType())){
                // 心跳包过滤
                return;
            }
            //ThreadPoolUtil.getEventThreadPool().execute(new OnEventTask(message));
        } catch (Exception ignored) {

        }

    }

    @OnClose
    public void onClose(Session session) throws InterruptedException {
        log.info("连接关闭, 尝试重连");
        boolean success = false;
        while (!success) {
            success = Client.connect("ws://192.168.5.135:8888");
            if (success) {
                log.info("重连成功");
            } else {
                log.error("重连失败, 5s后重试");
                Thread.sleep(5000);
            }
        }
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("连接异常, 异常信息 {}", throwable.getMessage());
    }

}
