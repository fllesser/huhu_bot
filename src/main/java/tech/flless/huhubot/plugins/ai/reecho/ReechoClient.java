package tech.flless.huhubot.plugins.ai.reecho;


import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;

import retrofit2.http.*;
import tech.flless.huhubot.config.GlobalConfig;
import tech.flless.huhubot.core.exception.FinishedException;
import tech.flless.huhubot.plugins.ai.reecho.entity.req.AsyncGenerateReqBody;
import tech.flless.huhubot.plugins.ai.reecho.entity.req.SyncGenerateReqBody;
import tech.flless.huhubot.plugins.ai.reecho.entity.resp.*;
import tech.flless.huhubot.utils.ThreadPoolUtil;
import tech.flless.huhubot.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RetrofitClient(baseUrl = "https://v1.reecho.cn")
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

    default RoleList getVoiceList() {
        return getVoiceList(GlobalConfig.reechoCf.authorization());
    }


    default String generate(String voiceId, String text) throws ExecutionException, InterruptedException, TimeoutException {
        SyncGenerateReqBody reqBody = new SyncGenerateReqBody(voiceId, text, "default");
        Future<Result<SyncGenerateData>> future = ThreadPoolUtil.ReechoExecutor.submit(
                () -> syncGenerate(GlobalConfig.reechoCf.authorization(), reqBody));
        return future.get(60, TimeUnit.SECONDS).getData().getAudio();
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






