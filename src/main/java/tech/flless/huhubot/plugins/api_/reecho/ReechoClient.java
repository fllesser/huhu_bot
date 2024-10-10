package tech.flless.huhubot.plugins.api_.reecho;


import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;

import retrofit2.http.*;
import tech.flless.huhubot.plugins.api_.reecho.entity.GenReqBody;
import tech.flless.huhubot.plugins.api_.reecho.entity.GenResp;


@RetrofitClient(baseUrl = "https://v1.reecho.cn/api")
public interface ReechoClient {

    @POST("/tts/generate")
    @Headers({
            "referer: https://dash.reecho.ai/",
            "Content-Type: application/json",
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36"
    })
    String generate(@Header("Authorization") String token, @Body GenReqBody body);



    @POST("/tts/generate/{id}")
    String generateWithId(@Header("Authorization") String key, @Path("id") String id);


    @POST("/tts/voice}")
    String voice(@Header("Authorization") String key, @Query("show") String show);


}






