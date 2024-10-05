package tech.flless.huhubot.plugins.ai.entity;


import lombok.Data;

@Data
public class CompletionRes {

    private String id;
    private String object;
    private long created;
    private String result;
    private boolean is_truncated;
    private boolean need_clear_history;

}