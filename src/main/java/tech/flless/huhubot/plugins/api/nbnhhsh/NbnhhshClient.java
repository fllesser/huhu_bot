package tech.flless.huhubot.plugins.api.nbnhhsh;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.util.List;
import java.util.Map;

/**
 * @author elastic chow
 * &#064;date  26/7/2023
 */
@RetrofitClient(baseUrl = "https://lab.magiconch.com/api/nbnhhsh/")
public interface NbnhhshClient {

    @POST("guess")
    @Headers({
            "origin: https://lab.magiconch.com",
            "referer: https://lab.magiconch.com/nbnhhsh/",
            "user-agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36",
            "Content-Type: application/json"
    })
    SxResult[] guess(@Body Map<String, String> body);

    default List<String> defaultGuess(String word) {
        SxResult[] resp = guess(Map.of("text", word));
        return resp.length == 0 ? null : resp[0].getTrans();
    }

}
