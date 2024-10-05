package tech.flless.huhubot.plugins.ai;


import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.http.*;
import tech.flless.huhubot.plugins.ai.entity.TokenRes;
import tech.flless.huhubot.plugins.ai.entity.WxMessage;

import java.util.ArrayList;
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
    @Headers({"Content-Type:application/json"})
    @FormUrlEncoded
    String getCompletion(@Query("access_token") String accessToken, @Field("messages") ArrayList<WxMessage> messages);
}
