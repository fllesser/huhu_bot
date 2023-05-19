package tech.chowyijiu.huhu_bot.entity.gocq.message;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import tech.chowyijiu.huhu_bot.constant.CqTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author elastic chow
 * @date 19/5/2023
 */
@Data
public class MessageSegment {
    //text, at...
    private final String type;
    private final Map<String, String> data = new HashMap<>();

    public void addParam(String key, String value) {
        data.put(key, value);
    }

    public static MessageSegment text(String text) {
        MessageSegment ms = new MessageSegment(CqTypeEnum.text.name());
        ms.addParam("text", text);
        return ms;
    }

    public static MessageSegment at(Long userId) {
        MessageSegment ms = new MessageSegment(CqTypeEnum.at.name());
        ms.addParam("qq", userId.toString());
        return ms;
    }

    public static MessageSegment image(String url) {
        MessageSegment ms = new MessageSegment(CqTypeEnum.image.name());
        ms.addParam("file", url);
        ms.addParam("subType", "0");
        return ms;
    }


    public String toJsonString() {
        return JSONObject.toJSONString(this);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[CQ:").append(type);
        data.keySet().forEach(key -> sb.append(",").append(key).append("=").append(data.get(key)));
        sb.append("]");
        return sb.toString();
    }
}