package tech.flless.huhubot.plugins.ai.entity;

import lombok.*;

import java.util.ArrayList;



@Data
@AllArgsConstructor
public class WxMessage {
    private String role;
    private String content;
}