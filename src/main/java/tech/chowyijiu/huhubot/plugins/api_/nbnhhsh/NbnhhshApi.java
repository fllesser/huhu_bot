package tech.chowyijiu.huhubot.plugins.api_.nbnhhsh;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

import java.util.List;
import java.util.Map;

/**
 * @author elastic chow
 * @date 26/7/2023
 */
@RetrofitClient(baseUrl = "https://lab.magiconch.com/api/nbnhhsh/")
public interface NbnhhshApi {

    Map<String, String> headers = Map.of(
            "origin", "https://lab.magiconch.com",
            "referer", "https://lab.magiconch.com/nbnhhsh/",
            "user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36",
            "Content-Type", "application/json"
    );

    @POST("guess")
    SxResult[] guess(@HeaderMap Map<String, String> headers, @Body Map<String, String> body);

    default List<String> defaultGuess(String word) {
        SxResult[] resp = guess(headers, Map.of("text", word));
        return resp.length == 0 ? null : resp[0].getTrans();
    }

}
