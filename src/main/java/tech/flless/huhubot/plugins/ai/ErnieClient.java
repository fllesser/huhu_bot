package tech.flless.huhubot.plugins.ai;


import com.alibaba.fastjson2.JSONObject;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import lombok.Getter;
import lombok.Setter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.http.*;
import tech.flless.huhubot.config.WxConfig;
import tech.flless.huhubot.plugins.ai.entity.CompletionRes;
import tech.flless.huhubot.plugins.ai.entity.TokenRes;
import tech.flless.huhubot.plugins.ai.entity.WxMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RetrofitClient(baseUrl = "https://aip.baidubce.com")
public interface ErnieClient {

    static OkHttpClient.Builder okhttpClientBuilder() {
        return new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

    }


    @POST("/oauth/2.0/token?grant_type=client_credentials")
    @Headers({"Content-Type:application/json", "Accept:application/json"})
    TokenRes getToken(@Query("client_id") String ak, @Query("client_secret") String sk);

    @POST("/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    CompletionRes getCompletion(@Query("access_token") String accessToken, @Body RequestBody body);


}
