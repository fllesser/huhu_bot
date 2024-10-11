package tech.flless.huhubot.plugins.ai.reecho.entity;

import lombok.Data;

import java.util.List;

@Data
public class AudioResp {

    private int status;
    private String message;
    private Data_ data;

    @Data
    public static class Data_ {
        private Metadata metadata;
    }


    @Data
    public static class Metadata {
        private List<Content> contents;
//        private int characters;
//        private List<Voice> voices;
//        private double temperature;
//        @JsonProperty("top_k")
//        private int topK;
//        @JsonProperty("top_p")
//        private double topP;
//        @JsonProperty("filter_top_k")
//        private int filterTopK;
//        private int framesToKeep;
//        private boolean isPremium;
//        @JsonProperty("break_clone")
//        private boolean breakClone;
//        private boolean enhance;
//        private boolean flash;
//        private boolean sharpen;
//        private boolean srt;
//        private int volume;
//        private String audio;
        //private Date generatedAt;
    }


    @Data
    public static class Content {
        private String audio;
//        private String voiceId;
//        private String text;
//        private double duration;
//        private Date generatedAt;
    }

//    public static class Voice {
//
//        private String id;
//        private String status;
//        private String type;
//        private String name;
//        private String tags;
//        private int views;
//        private int clicks;
//        private int likes;
//        private int used;
//        private int generated;
//        private Metadata metadata;
//        private Date createdAt;
//        private Date updatedAt;
//        private String userId;
//        private String originVoiceId;
//        private String avatar;
//        private String version;
//        private String from;
//
//    }
}
