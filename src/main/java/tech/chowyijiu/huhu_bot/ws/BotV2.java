package tech.chowyijiu.huhu_bot.ws;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.constant.ANSI;
import tech.chowyijiu.huhu_bot.constant.GocqActionEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.request.RequestBox;
import tech.chowyijiu.huhu_bot.exception.gocq.ActionFailed;
import tech.chowyijiu.huhu_bot.utils.LogUtil;
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

    /***
     * 等待响应
     * @param echo 回声
     * @return String
     */
    public String waitResp(String echo) {
        if (!StringUtil.hasLength(echo)) log.info("echo is empty, ignored");
        log.info("Blocking waits for gocq to return the result, echo: {}", echo);
        LinkedBlockingDeque<String> blockingRes = new LinkedBlockingDeque<>(1);
        respMap.put(echo, blockingRes);
        try {
            return blockingRes.poll(BotV2.timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new ActionFailed("等待响应数据线程中断异常, echo:" + echo);
        } finally {
            respMap.remove(echo);
        }
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
                    LogUtil.buildArgsWithColor(ANSI.YELLOW, this.session.getId(), text, e.getMessage()));
        }
    }

    private String callApi(GocqActionEnum action, Map<String, Object> paramsMap, boolean needReturn) {
        RequestBox requestBox = new RequestBox();
        requestBox.setAction(action.getAction());
        Optional.ofNullable(paramsMap).ifPresent(requestBox::setParams);
        if (needReturn) {
            String echo = Thread.currentThread().getName() + "_" +
                    this.getUserId() + "_" +
                    action.getAction() + "_" +
                    UUID.randomUUID().toString().replace("-", "");
            requestBox.setEcho(echo);
            //发送请求
            this.sessionSend(JSONObject.toJSONString(requestBox));
            return waitResp(echo);
        } else {
            this.sessionSend(JSONObject.toJSONString(requestBox));
            return "";
        }
    }


}
