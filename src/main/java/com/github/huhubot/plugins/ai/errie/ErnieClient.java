package com.github.huhubot.plugins.ai.errie;


import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import org.springframework.stereotype.Component;
import retrofit2.http.*;
import com.github.huhubot.plugins.ai.errie.entity.CompletionRes;
import com.github.huhubot.plugins.ai.errie.entity.TokenRes;
import com.github.huhubot.plugins.ai.errie.entity.WxMessages;

@Component
@RetrofitClient(baseUrl = "https://aip.baidubce.com", readTimeoutMs = 30000)
public interface ErnieClient {

    @POST("/oauth/2.0/token?grant_type=client_credentials")
    @Headers({"Content-Type:application/json", "Accept:application/json"})
    TokenRes getToken(@Query("client_id") String clientId, @Query("client_secret") String clientSecret);

    @POST("/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    CompletionRes getCompletion(@Query("access_token") String accessToken, @Body WxMessages messages);

}
