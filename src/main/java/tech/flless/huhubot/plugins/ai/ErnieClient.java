package tech.flless.huhubot.plugins.ai;


import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.http.*;
import tech.flless.huhubot.plugins.ai.entity.CompletionRes;
import tech.flless.huhubot.plugins.ai.entity.TokenRes;
import tech.flless.huhubot.plugins.ai.entity.WxMessage;
import tech.flless.huhubot.plugins.ai.entity.WxMessages;

import java.util.List;
import java.util.Map;

@RetrofitClient(baseUrl = "https://aip.baidubce.com")
public interface ErnieClient {


    @POST("/oauth/2.0/token?grant_type=client_credentials")
    @Headers({"Content-Type:application/json", "Accept:application/json"})
    TokenRes getToken(@Query("client_id") String clientId, @Query("client_secret") String clientSecret);

    @POST("/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    CompletionRes getCompletion(@Query("access_token") String accessToken, @Body WxMessages messages);

}
