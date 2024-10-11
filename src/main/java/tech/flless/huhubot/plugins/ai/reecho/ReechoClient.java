package tech.flless.huhubot.plugins.ai.reecho;


import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;

import retrofit2.http.*;
import tech.flless.huhubot.config.ReechoConfig;
import tech.flless.huhubot.core.exception.FinishedException;
import tech.flless.huhubot.core.utils.ThreadPoolUtil;
import tech.flless.huhubot.plugins.ai.reecho.entity.GenReqBody;
import tech.flless.huhubot.plugins.ai.reecho.entity.GenResp;
import tech.flless.huhubot.plugins.ai.reecho.entity.RoleList;
import tech.flless.huhubot.plugins.ai.reecho.entity.AudioResp;
import tech.flless.huhubot.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RetrofitClient(baseUrl = "https://v1.reecho.cn")
public interface ReechoClient {

    @POST("/api/tts/generate")
    @Headers({
            "referer: https://dash.reecho.ai/",
            "content-type: application/json",
            "user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36"
    })
    GenResp generate(@Header("authorization") String token, @Body GenReqBody body);


    @GET("/api/tts/generate/{id}")
    AudioResp generateWithId(@Header("Authorization") String key, @Path("id") String id);


    @GET("/api/tts/voice?show=full")
    RoleList getVoiceList(@Header("Authorization") String key);

    Map<String, String> NameIdMap = new HashMap<>();

    @SuppressWarnings("all")
    default String generate(String name, String text) {
        if (NameIdMap.size() == 0) {
            RoleList roleList = getVoiceList(ReechoConfig.apiKey);
            roleList.getData().forEach(role -> NameIdMap.put(role.getName(), role.getId()));
        }
        if (!NameIdMap.containsKey(name)) throw new FinishedException("未支持角色[" + name + "]" + "\n请发送[角色列表]查看支持的角色");
        GenResp resp = generate(ReechoConfig.webToken, new GenReqBody("market:" + NameIdMap.get(name), text));
        if (resp.getData() == null) throw new FinishedException("今日点数已用尽，明天再来吧");
        try {
            return ThreadPoolUtil.getScheduledExecutor().schedule(() -> {
                String audioUrl;
                do {
                    AudioResp audioResp = this.generateWithId(ReechoConfig.webToken, resp.getData().getId());
                    audioUrl = audioResp.getData().getMetadata().getContents().get(0).getAudio();
                    Thread.sleep(1000);
                } while (!StringUtil.hasLength(audioUrl));
                return audioUrl;
            }, 200 + text.length() * 100 , TimeUnit.MILLISECONDS).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}






