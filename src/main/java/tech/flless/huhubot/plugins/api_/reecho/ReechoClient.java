package tech.flless.huhubot.plugins.api_.reecho;


import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import retrofit2.http.*;
import tech.flless.huhubot.config.ReechoConfig;
import tech.flless.huhubot.plugins.api_.reecho.entity.GenReqBody;
import tech.flless.huhubot.plugins.api_.reecho.entity.GenResp;
import tech.flless.huhubot.plugins.api_.reecho.entity.VoiceResp;
import tech.flless.huhubot.utils.StringUtil;

import java.util.concurrent.*;

import static tech.flless.huhubot.plugins.api_.reecho.VoiceIdEnum.LeiJun;

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
    VoiceResp generateWithId(@Header("Authorization") String key, @Path("id") String id);


    @GET("/api/tts/voice?show=full")
    String getVoiceList(@Header("Authorization") String key);

    //ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    default String generate(VoiceIdEnum voice, String text) throws InterruptedException {
        GenResp resp = generate(ReechoConfig.webToken, new GenReqBody(voice.getVoiceId(), text));
        //Action action = new Action(this, resp);
        //return executor.schedule(action, 0, TimeUnit.SECONDS).get();
        String audioUrl;
        do {
            Thread.sleep(1000);
            VoiceResp voiceResp = generateWithId(ReechoConfig.webToken, resp.getData().getId());
            audioUrl = voiceResp.getData().getMetadata().getContents().get(0).getAudio();
        } while (!StringUtil.hasLength(audioUrl));
        return audioUrl;

    }

//    record Action(ReechoClient client, GenResp genResp) implements Callable<String>{
//
//        @Override
//        public String call() throws Exception {
//            VoiceResp voiceResp = client.generateWithId(ReechoConfig.webToken, genResp.getData().getId());
//            String audioUrl = voiceResp.getData().getMetadata().getContents().get(0).getAudio();
//            return StringUtil.hasLength(audioUrl) ? audioUrl : "";
//        }
//
//    }
}






