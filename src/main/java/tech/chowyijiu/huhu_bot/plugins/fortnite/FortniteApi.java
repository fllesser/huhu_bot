package tech.chowyijiu.huhu_bot.plugins.fortnite;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fortnite API Wrapper
 *
 * @author elastic chow
 * @date 5/6/2023
 */
public class FortniteApi {

    private static final String API_KEY = "f3f4e682-346e-45b1-8323-fe77aaea2a68";
    private static final String API_URL = "https://fortnite-api.com";

    private static JSONObject getData(final String json) {
        return ((JSONObject) JSONObject.parseObject(json).get("data"));
    }

    public static HttpRequest apiRequest(String urlSuffix) {
        return HttpRequest.get(API_URL + urlSuffix).form("language", "zh-CN");
    }

    public static JSONObject getShop() {
        return getData(apiRequest("/v2/shop/br/combined").execute().body());
    }

    public static List<ShopEntry> getShopEntries() {
        List<ShopEntry> shopEntries = new ArrayList<>();
        JSONObject shop = getShop();
        String[] types = {"featured", "daily", "votes", "voteWinners"};
        for (String type : types) {
            Optional.ofNullable(shop.get(type)).ifPresent(item -> {
                shopEntries.addAll(((JSONArray) ((JSONObject) item).get("entries")).toList(ShopEntry.class));
            });
        }
        return shopEntries;
    }

    public static JSONObject getCosmeticsList() {
        return getData(apiRequest("/v2/cosmetics/br").execute().body());
    }

    public static JSONObject getNewCosmetics() {
        return getData(apiRequest("/v2/cosmetics/br/new").execute().body());
    }

    public static JSONObject getBrMap() {
        return getData(apiRequest("/br/map").execute().body());
    }
}
