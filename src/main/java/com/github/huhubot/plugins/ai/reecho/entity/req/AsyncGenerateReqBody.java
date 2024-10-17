package com.github.huhubot.plugins.ai.reecho.entity.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
public class AsyncGenerateReqBody {

    private String model = "reecho-neural-voice-001";
    private List<Content> contents;
    private Float temperature = 0.98f;
    @JsonProperty("top_k")
    private Integer top_k = 256;
    @JsonProperty("top_p")
    private Float top_p = 0.93f;
    @JsonProperty("frames_to_keep")
    private Integer frames_to_keep = 5;
    private Boolean enhance = false;
    @JsonProperty("filter_top_k")
    private Integer filter_top_k = 0;
    @JsonProperty("break_clone")
    private Boolean break_clone = true;
    private Boolean sharpen = false;
    private Integer seed = -1;
    private Boolean srt = false;
    private Integer volume = 1;

    @Data
    @AllArgsConstructor
    public static class Content {
        private String voiceId;
        private String text;
    }

    public AsyncGenerateReqBody(String voiceId, String text) {
        contents = List.of(new Content(voiceId, text));
    }
}
