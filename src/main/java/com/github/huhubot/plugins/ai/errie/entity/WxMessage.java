package com.github.huhubot.plugins.ai.errie.entity;

import lombok.*;


@Data
@AllArgsConstructor
public class WxMessage {
    private String role;
    private String content;
}