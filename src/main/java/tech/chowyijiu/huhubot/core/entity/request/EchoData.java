package tech.chowyijiu.huhubot.core.entity.request;

import tech.chowyijiu.huhubot.core.exception.ActionFailed;

import java.util.HashMap;
import java.util.Map;

/**
 * @author flless
 * @date 17/8/2023
 */
public class EchoData {

    private final String echo;
    private String data;

    private EchoData(String echo) {
        this.echo = echo;
    }

    private static final long timeout = 10000L;
    private static final Map<String, EchoData> ECHO_DATA_MAP = new HashMap<>();

    public static EchoData newInstance(String echo) {
        EchoData echoData = new EchoData(echo);
        ECHO_DATA_MAP.put(echo, echoData);
        return echoData;
    }

    public static EchoData getByEcho(String echo) {
        return ECHO_DATA_MAP.get(echo);
    }

    public String waitGetData() {
        try {
            this.wait(timeout);
        } catch (InterruptedException e) {
            throw new ActionFailed("等待响应数据, 出现线程中断异常, echo:" + echo);
        } finally {
            ECHO_DATA_MAP.remove(echo);
        }
        return data;
    }

    public synchronized void setData(String data) {
        this.data = data;
        this.notify();
    }


}
