package tech.chowyijiu.huhu_bot.ws;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.constant.ANSI;
import tech.chowyijiu.huhu_bot.constant.GocqAction;
import tech.chowyijiu.huhu_bot.entity.request.RequestBox;
import tech.chowyijiu.huhu_bot.exception.ActionFailed;
import tech.chowyijiu.huhu_bot.utils.StringUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @author elastic chow
 * @date 30/6/2023
 */
@Slf4j
@Getter
@ToString
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class BotV2 {

    private final Long userId;
    private final WebSocketSession session;

    public static final long timeout = 5000L;
    private final Map<String, LinkedBlockingDeque<String>> respMap = new HashMap<>();

    public void putEchoResult(String echo, String data) {
        if (respMap.containsKey(echo)) respMap.get(echo).offer(data);
    }

    /**
     * call api 最终调用的方法
     * Send a WebSocket message
     *
     * @param text text
     */
    public void sessionSend(String text) {
        try {
            this.session.sendMessage(new TextMessage(text));
        } catch (IOException e) {
            log.info("{}sessionSend error, session[{}], message[{}], exception[{}]{}",
                    ANSI.YELLOW, this.session.getId(), text, e.getMessage(), ANSI.RESET);
        }
    }

    private String callApi(GocqAction action, Map<String, Object> paramsMap) {
        RequestBox requestBox = new RequestBox();
        requestBox.setAction(action.name());
        Optional.ofNullable(paramsMap).ifPresent(requestBox::setParams);
        if (action.isHasResp()) {
            String echo = Thread.currentThread().getName() + "_" +
                    this.getUserId() + "_" +
                    action + "_" +
                    UUID.randomUUID().toString().replace("-", "");
            requestBox.setEcho(echo);
            LinkedBlockingDeque<String> blockingRes = new LinkedBlockingDeque<>(1);
            respMap.put(echo, blockingRes);
            //发送请求
            this.sessionSend(JSONObject.toJSONString(requestBox));
            log.info("Blocking waits for gocq to return the result, echo: {}", echo);
            try {
                String resp = blockingRes.poll(BotV2.timeout, TimeUnit.MILLISECONDS);
                if (!StringUtil.hasLength(resp))
                    throw new ActionFailed("echo:" + echo + ", api请求超时, 或者该api无响应数据");
                log.info("{}Accepted a response for echo:{}{}", ANSI.BLUE, echo, ANSI.RESET);
                return resp;
            } catch (InterruptedException e) {
                throw new ActionFailed("等待响应数据线程中断异常, echo:" + echo);
            } finally {
                respMap.remove(echo);
            }
        } else {
            this.sessionSend(JSONObject.toJSONString(requestBox));
            return "";
        }
    }


}
