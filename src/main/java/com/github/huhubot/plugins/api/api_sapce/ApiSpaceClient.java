package com.github.huhubot.plugins.api.api_sapce;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @author FLLess7
 * &#064;date  22/1/2024
 */
@SuppressWarnings("unused")
@RetrofitClient(baseUrl = "https://eolink.o.apispace.com")
public interface ApiSpaceClient {

    @POST("zgjm/common/dream/searchDreamDetail")
    @FormUrlEncoded
    ApiSpaceResult zgjm(@Header ("X-APISpace-Token") String token, @Field("keyword") String keyword);

    @POST("pic2sketch/api/v1/forward/pic2sketch")
    @FormUrlEncoded
    ApiSpaceResult sketch(@Header ("X-APISpace-Token") String token, @Field("image_url") String url);
}
