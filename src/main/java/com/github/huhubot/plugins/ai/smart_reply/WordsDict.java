package com.github.huhubot.plugins.ai.smart_reply;

import cn.hutool.http.HttpUtil;
import com.github.huhubot.config.GlobalConfig;
import com.github.huhubot.plugins.ai.reecho.ReechoClient;
import com.github.huhubot.plugins.ai.reecho.ReechoUtil;
import com.github.huhubot.utils.IocUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WordsDict {

    private static final List<String> words;

    static {
        String[] wordArr = new String[]{
                "老色批，你再戳？",
                "连个可爱美少女都要戳的肥宅真恶心啊。",
                "你再戳！",
                "？再戳试试？",
                "别戳了别戳了再戳就坏了555",
                "我爪巴爪巴，球球别再戳了",
                "你戳你马呢？！",
                "请不要戳" + GlobalConfig.botCf.getNickName() + ">_<",
                "放手啦，不给戳QAQ",
                "喂(#`O′) 戳" + GlobalConfig.botCf.getNickName() + "干嘛！",
                "戳坏了，赔钱！",
                "戳坏了",
                "嗯……不可以……啦……不要乱戳",
                "那...那里...那里不能戳...绝对...",
                "有事恁叫我，别天天一个劲戳戳戳！",
                "欸很烦欸！你戳锤子呢",
                "再戳一下试试？",
                "正在关闭对您的所有服务...关闭成功",
                "啊呜，太舒服刚刚竟然睡着了。什么事？",
                "正在定位您的真实地址...定位成功。轰炸机已起飞"};
        words = new ArrayList<>(wordArr.length);
        words.addAll(Arrays.asList(wordArr));
    }


    public static String randWord() {
        return words.get((int) (Math.random() * words.size()));
    }

    public static String randVoice() {
        int wordId = (int) (Math.random() * words.size());
        ReechoClient reechoClient = IocUtil.getBean(ReechoClient.class);
        String id = ReechoUtil.randId();
        String voicePath = System.getProperty("user.dir") + File.separator + "voices" + File.separator + wordId + "_" + id + ".mp3";
        if (!new File(voicePath).exists()) {
            String audioUrl = reechoClient.generate(id, words.get(wordId));
            HttpUtil.downloadFile(audioUrl, voicePath);
        }
        return voicePath;
    }
}
