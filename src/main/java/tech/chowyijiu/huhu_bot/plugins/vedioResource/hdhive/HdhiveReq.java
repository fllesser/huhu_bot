package tech.chowyijiu.huhu_bot.plugins.vedioResource.hdhive;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import tech.chowyijiu.huhu_bot.utils.StringUtil;

import java.util.Map;

/**
 * @author elastic chow
 * @date 19/7/2023
 */
public class HdhiveReq {

    private static final String auth = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTY4OTY2NTAxMywianRpIjoiNDA3ODBkYjUtMjRjOS00ZjE5LTgyOTMtMGFhZGUxYWEzMjY5IiwidHlwZSI6ImFjY2VzcyIsInN1YiI6NTE5NiwibmJmIjoxNjg5NjY1MDEzLCJleHAiOjE2OTIyNTcwMTN9.uCBvHlfROMm6yrqN5CVKTV4s1w8GOeAj51fiDg7VlAo";
    private static final String url1 = "https://www.hdhive.org/api/v1/public/tmdb/search/multi";
    private static final String url2 = "https://www.hdhive.org/api/v1/public/";
    private static final String url3 = "https://www.hdhive.org/api/v1/customer/resources";

    /**
     * 搜索 by page
     */
    public static String get1(String keyword) {
        Map<String, Object> map = Map.of("query", keyword, "page", 1);
        String respJson = HttpUtil.get(url1, map);
        JSONObject jsonObject = JSONObject.parseObject(respJson);
        JSONArray data = jsonObject.getJSONArray("data");
        StringBuilder sb = new StringBuilder();
        if (data.size() == 0) return "无此关键词资源";
        else sb.append("共查询到以下资源:");
        for (Object d : data) {
            JSONObject d_ = (JSONObject) d;
            Integer tmdbId = d_.getInteger("id");
            String mediaType = d_.getString("media_type");
            switch (mediaType) {
                case "movie" -> sb.append(get2("movies", tmdbId));
                case "tv" -> sb.append(get2("tv", tmdbId));
                default -> {
                }
            }
        }
        return sb.toString();
    }

    /**
     * 进入详细页面
     */
    private static String get2(String type, Integer tmdbId) {
        String respJson = HttpUtil.get(url2 + type, Map.of("tmdb_id", tmdbId));
        JSONObject jsonObject = JSONObject.parseObject(respJson);
        JSONArray dataArr = jsonObject.getJSONArray("data");
        String res = "";
        for (Object d : dataArr) {
            JSONObject d_ = (JSONObject) d;
            Integer id = d_.getInteger("id");
            switch (type) {
                case "tv" -> res = get3("tv_id", id);
                case "movies" -> res = get3("movie_id", id);
                default -> {
                }
            }
            if (!StringUtil.hasLength(res)) res = "\n" + d_.getString("name") + " 暂无分享";
        }
        return res;
    }

    /**
     * 获得分享链接
     */
    private static String get3(String idType, Integer id) {
        Map<String, Object> map = Map.of(idType, id,
                "sort_by", "is_admin", "sort_order", "descend", "per_page", 100);
        String respJson = HttpRequest.get(url3)
                .bearerAuth(auth).form(map)
                .execute()
                .body();
        JSONObject jsonObject = JSONObject.parseObject(respJson);
        Boolean success = jsonObject.getBoolean("success");
        if (success == null || !success) return "auth可能过期, 请联系我";
        JSONArray data = jsonObject.getJSONArray("data");
        if (data == null || data.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (Object d : data) {
            JSONObject d_ = (JSONObject) d;
            sb.append("\n").append(d_.get("title")).append(d_.get("remark")).append(" ").append(d_.get("url"));
        }
        return sb.toString();
    }
}
