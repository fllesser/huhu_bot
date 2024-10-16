package tech.flless.huhubot.plugins.ai.reecho.entity.resp;


import lombok.Data;

@Data
public class SyncGenerateData {
    private String id;
    private String audio;
    private String streamUrl;
    private int credit_used;
}