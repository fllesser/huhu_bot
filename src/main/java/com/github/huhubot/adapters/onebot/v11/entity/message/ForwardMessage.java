package com.github.huhubot.adapters.onebot.v11.entity.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.github.huhubot.core.exception.IllegalMessageTypeException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ForwardMessage node 这是自定义转发消息的一个node, 发送即为ForwardMessage[]
 *
 * @author elastic chow
 * @date 14/5/2023
 */
@Data
public class ForwardMessage {
    private final String type = "node";

    private Node data;

    private ForwardMessage() {

    }

    private static ForwardMessage instance(String name, Long uin, Object content) {
        if (!(content instanceof String) && !(content instanceof MessageSegment) && !(content instanceof Message))
            throw new IllegalMessageTypeException();
        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setData(new Node(name, uin, content));
        return forwardMessage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Node {
        @JsonProperty("nickname")
        private String name;    //发送者显示名字	用于自定义消息 (自定义消息并合并转发, 实际查看顺序为自定义消息段顺序)
        @JsonProperty("user_id")
        private Long uin;       //uin	int64	发送者QQ号	用于自定义消息
        private Object content; //用MessageSegment组合构建
    }

    /**
     * 快速构建 name uin 恒定
     *
     * @param name     name
     * @param uin      userId
     * @param messages messages Object 可以为Message MessageSegment String
     * @return List<ForwardMessage>
     */
    public static List<ForwardMessage> quickBuild(String name, Long uin, List<Object> messages) {
        return messages.stream()
                .map(message -> ForwardMessage.instance(name, uin, message))
                .collect(Collectors.toList());
    }

    /**
     * 快速构建 uin 恒定, name message 对应, 作为键值对存储, 用于生成排行榜类
     *
     * @param uin         Long
     * @param nameAndMsgs LinkedHashMap<String, Object> 需要插入顺序, Object 可以为Message MessageSegment String
     * @return List<ForwardMessage>
     */
    public static List<ForwardMessage> quickBuild(Long uin, LinkedHashMap<String, Object> nameAndMsgs) {
        return nameAndMsgs.keySet().stream()
                .map(name -> ForwardMessage.instance(name, uin, nameAndMsgs.get(name)))
                .collect(Collectors.toList());
    }
}