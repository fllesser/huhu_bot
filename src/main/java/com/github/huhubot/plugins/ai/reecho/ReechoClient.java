package com.github.huhubot.plugins.ai.reecho;


import com.github.huhubot.plugins.ai.reecho.entity.resp.*;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;
import retrofit2.http.*;
import com.github.huhubot.config.GlobalConfig;
import com.github.huhubot.core.exception.FinishedException;
import com.github.huhubot.plugins.ai.reecho.entity.req.AsyncGenerateReqBody;
import com.github.huhubot.plugins.ai.reecho.entity.req.SyncGenerateReqBody;
import com.github.huhubot.utils.ThreadPoolUtil;
import com.github.huhubot.utils.StringUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RetrofitClient(baseUrl = "https://v1.reecho.cn", readTimeoutMs = 300000)
public interface ReechoClient {

    @POST("/api/tts/generate")
    @Headers({
            "referer: https://dash.reecho.ai/",
            "content-type: application/json",
            "user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36"
    })
    AsyncGenResp asyncGenerate(@Header("authorization") String token, @Body AsyncGenerateReqBody body);


    @GET("/api/tts/generate/{id}")
    AudioResp asyncGenerate(@Header("Authorization") String key, @Path("id") String id);

    @POST("/api/tts/simple-generate")
    Result<SyncGenerateData> syncGenerate(@Header("Authorization") String key, @Body SyncGenerateReqBody body);

    @GET("/api/tts/voice")
    RoleList getVoiceList(@Header("Authorization") String key);

    @GET("/api/account/info")
    AccountInfo getAccountInfo(@Header("Authorization") String key);

    default RoleList getVoiceList() {
        return getVoiceList(GlobalConfig.reechoCf.authorization());
    }


    default String generate(String voiceId, String text)  {
        SyncGenerateReqBody reqBody = new SyncGenerateReqBody(voiceId, text, "default");
        Future<Result<SyncGenerateData>> future = ThreadPoolUtil.ReechoExecutor.submit(
                () -> syncGenerate(GlobalConfig.reechoCf.authorization(), reqBody));
        try {
            return future.get(300, TimeUnit.SECONDS).getData().getAudio();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new FinishedException("合成超时");
        }
    }


    @Deprecated
    default String oldGenerate(String voiceId, String text) {
        AsyncGenResp resp = asyncGenerate(GlobalConfig.reechoCf.getWebToken(), new AsyncGenerateReqBody("market:" + voiceId, text));
        if (resp.getData() == null) throw new FinishedException("请求失败，今日点数可能已耗尽");
        try {
            return ThreadPoolUtil.ReechoExecutor.submit(() -> {
                String audioUrl;
                int times = 0;
                do {
                    Thread.sleep(1000);
                    AudioResp audioResp = this.asyncGenerate(GlobalConfig.reechoCf.authorization(), resp.getData().getId());
                    audioUrl = audioResp.getData().getMetadata().getContents().get(0).getAudio();
                } while (!StringUtil.hasLength(audioUrl) && ++times < 30);
                return audioUrl;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}






