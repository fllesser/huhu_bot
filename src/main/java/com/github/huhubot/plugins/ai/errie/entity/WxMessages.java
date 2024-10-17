package com.github.huhubot.plugins.ai.errie.entity;

import lombok.Data;

import java.util.List;

@Data
public class WxMessages {
    private List<WxMessage> messages;

    public WxMessages(String content) {
        this.messages = List.of(new WxMessage("user", content));
    }
}
