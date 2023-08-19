package tech.chowyijiu.huhubot.core.ws;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhubot.core.constant.ANSI;

import java.io.IOException;

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
                    ANSI.YELLOW, this.session.getId(), text, e.getMessage(), ANSI.RESET);
        }
    }

    //@SuppressWarnings("all")
    //private String callApi(GocqAction action, Map<String, Object> paramsMap) {
    //    RequestBox requestBox = new RequestBox();
    //    requestBox.setAction(action.name());
    //    Optional.ofNullable(paramsMap).ifPresent(requestBox::setParams);
    //    if (action.isHasResp()) {
    //        String echo = (this.getUserId() + "-" + action + "-" + Math.random());
    //        requestBox.setEcho(echo);
    //        EchoData echoData = EchoData.newInstance(echo);
    //        //因为可能存在当前线程还没wait, 其他线程就抢先获得了锁的情况, 所以先获取锁, 再发送ws请求
    //        synchronized (echoData) {
    //            this.sessionSend(JSONObject.toJSONString(requestBox));
    //            return echoData.waitGetData();
    //        }
    //    } else {
    //        this.sessionSend(JSONObject.toJSONString(requestBox));
    //        return "";
    //    }
    //}


}
