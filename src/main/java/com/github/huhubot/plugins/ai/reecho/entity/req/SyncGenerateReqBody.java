package com.github.huhubot.plugins.ai.reecho.entity.req;

import lombok.Data;


@Data
public class SyncGenerateReqBody {

    private String voiceId;
    private String text;
    //private List<String> texts;  //要生成的文本列表，优先级高于text参数
    //角色风格
    private String promptId;
    //多样性 0-100 default:98
    private int randomness = 98;
    //稳定性过滤 (0-1024，默认为256)
    private int stability_boost = 256;
    //概率优选（0-100，默认为93）
    private int probability_optimization = 93;
    //本文情感
    private boolean break_clone = true;
    //音质增强
    private boolean sharpen = false;
    private boolean flash = false;
    private boolean stream = false;
    private boolean srt = false;
    private int seed = -1;
    //private List<Dictionary> dictionary;

    public SyncGenerateReqBody(String voiceId, String text, String promptId)  {
        this.voiceId = voiceId;
        this.text = text;
        this.promptId = promptId;
    }
}
