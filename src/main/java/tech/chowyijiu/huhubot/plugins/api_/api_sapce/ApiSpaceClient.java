package tech.chowyijiu.huhubot.plugins.api_.api_sapce;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author FLLess7
 * @date 22/1/2024
 */
@RetrofitClient(baseUrl = "https://eolink.o.apispace.com")
public interface ApiSpaceClient {

    @POST("zgjm/common/dream/searchDreamDetail")
    @Headers({
        "X-APISpace-Token: 6j6vmwvo0n5a8szhqpay4dgkwo1x8ghy",
        "Content-Type: application/x-www-form-urlencoded"
    })
    String zgjm(@Query("keyword") String keyword);
}
