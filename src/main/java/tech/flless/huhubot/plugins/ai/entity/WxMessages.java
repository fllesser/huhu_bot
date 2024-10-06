package tech.flless.huhubot.plugins.ai.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WxMessages {
    private List<WxMessage> messages;

    public WxMessages(String content) {
        this.messages = List.of(new WxMessage("user", content));
    }
}
