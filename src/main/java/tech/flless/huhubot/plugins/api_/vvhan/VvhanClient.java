package tech.flless.huhubot.plugins.api_.vvhan;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author FLLess7
 * @date 17/9/2023
 */
@RetrofitClient(baseUrl = "https://api.vvhan.com/api")
public interface VvhanClient {

    @GET("moyu?type=json")
    MoyuResult moyu();

    @GET("qiang")
    QiangResult qiang(@Query("url") String url);
}