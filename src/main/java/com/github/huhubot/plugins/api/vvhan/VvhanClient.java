package com.github.huhubot.plugins.api.vvhan;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import org.springframework.stereotype.Component;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author FLLess7
 * &#064;date  17/9/2023
 */
@SuppressWarnings("unused")
@Component
@RetrofitClient(baseUrl = "https://api.vvhan.com/api")
public interface VvhanClient {

    @GET("moyu?type=json")
    MoyuResult moyu();

    @GET("qiang")
    QiangResult qiang(@Query("url") String url);
}