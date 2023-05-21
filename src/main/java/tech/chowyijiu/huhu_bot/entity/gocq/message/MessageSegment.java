package tech.chowyijiu.huhu_bot.entity.gocq.message;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import tech.chowyijiu.huhu_bot.constant.CqTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author elastic chow
 * @date 19/5/2023
 */
@Data
public class MessageSegment {
    //text, at...
    private final String type;
    private final List<Node> data = new ArrayList<>(3);

    @RequiredArgsConstructor
    static class Node {
        final String key;
        final String value;
    }


    public void addParam(String key, String value) {
        data.add(new Node(key, value));
    }

    public String getText() {
        Node node = data.get(0);
        if (node.key.equals("text")) {
            return node.value;
        }
        return null;
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
        data.forEach(node -> sb.append(",").append(node.key).append("=").append(node.value));
        sb.append("]");
        return sb.toString();
    }
}