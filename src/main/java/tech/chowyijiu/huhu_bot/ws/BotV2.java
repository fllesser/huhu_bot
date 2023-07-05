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
import tech.chowyijiu.huhu_bot.utils.GocqUtil;
import tech.chowyijiu.huhu_bot.utils.LogUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
        RequestBox<Map<String, Object>> requestBox = new RequestBox<>();
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
            return GocqUtil.waitResp(echo,5000L);
        } else {
            this.sessionSend(JSONObject.toJSONString(requestBox));
            return "";
        }
    }


}
