package tech.chowyijiu.huhubot.plugins.api_.api_sapce;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @author FLLess7
 * @date 22/1/2024
 */
@RetrofitClient(baseUrl = "https://eolink.o.apispace.com")
public interface ApiSpaceClient {


    @POST("zgjm/common/dream/searchDreamDetail")
    @FormUrlEncoded
    ApiSpaceResult zgjm(@Header ("X-APISpace-Token") String token, @Field("keyword") String keyword);
}
