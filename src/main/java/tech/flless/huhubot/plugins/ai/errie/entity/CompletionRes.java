package tech.flless.huhubot.plugins.ai.errie.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompletionRes {

    private String id;
    private String object;
    private long created;
    private String result;
    @JsonProperty("is_truncated")
    private boolean isTruncated;
    @JsonProperty("need_clear_history")
    private boolean needClearHistory;

}