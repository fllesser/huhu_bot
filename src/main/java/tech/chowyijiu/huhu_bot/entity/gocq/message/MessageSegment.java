package tech.chowyijiu.huhu_bot.entity.gocq.message;

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

    public MessageSegment addParam(String key, String value) {
        data.add(new Node(key, value));
        return this;
    }

    public String getText() {
        Node node = data.get(0);
        if (node.key.equals("text")) {
            return node.value;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[CQ:").append(type);
        data.forEach(node -> sb.append(",").append(node.key).append("=").append(node.value));
        sb.append("]");
        return sb.toString();
    }

    public static MessageSegment text(String text) {
        MessageSegment segment = new MessageSegment(CqTypeEnum.text.name());
        segment.addParam("text", text);
        return segment;
    }

    public static MessageSegment at(Long userId) {
        MessageSegment segment = new MessageSegment(CqTypeEnum.at.name());
        segment.addParam("qq", userId.toString());
        return segment;
    }

    public static MessageSegment image(String url) {
        MessageSegment segment = new MessageSegment(CqTypeEnum.image.name());
        segment.addParam("file", url).addParam("subType", "0");
        return segment;
    }

    public static MessageSegment poke(Long userId) {
        MessageSegment segment = new MessageSegment(CqTypeEnum.poke.name());
        segment.addParam("qq", userId.toString());
        return segment;
    }

    public static MessageSegment tts(String text) {
        MessageSegment segment = new MessageSegment(CqTypeEnum.tts.name());
        segment.addParam("text", text);
        return segment;
    }

}