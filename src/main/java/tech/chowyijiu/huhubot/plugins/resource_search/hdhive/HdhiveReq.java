package tech.chowyijiu.huhubot.plugins.resource_search.hdhive;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import tech.chowyijiu.huhubot.plugins.resource_search.cache_.ResourceData;

import java.util.ArrayList;
import java.util.List;
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
    public static List<ResourceData> get1(String keyword) {
        Map<String, Object> map = Map.of("query", keyword, "page", 1);
        String respJson = HttpUtil.get(url1, map);
        JSONObject jsonObject = JSONObject.parseObject(respJson);
        JSONArray data = jsonObject.getJSONArray("data");
        if (data.size() == 0) return null;
        List<ResourceData> dataList = new ArrayList<>();
        for (Object d : data) {
            JSONObject d_ = (JSONObject) d;
            Integer tmdbId = d_.getInteger("id");
            String mediaType = d_.getString("media_type");
            String type = switch (mediaType) {
                case "movie" -> "movies";
                case "tv" -> "tv";
                default -> mediaType;
            };
            ResourceData resourceData = get2(type, tmdbId);
            if (resourceData != null) dataList.add(resourceData);
        }
        return dataList;
    }

    /**
     * 进入详细页面
     */
    private static ResourceData get2(String type, Integer tmdbId) {
        String respJson = HttpUtil.get(url2 + type, Map.of("tmdb_id", tmdbId));
        JSONObject jsonObject = JSONObject.parseObject(respJson);
        JSONArray dataArr = jsonObject.getJSONArray("data");
        if (dataArr == null) return null;
        ResourceData res = null;
        for (Object d : dataArr) {
            JSONObject d_ = (JSONObject) d;
            JSONArray resources = d_.getJSONArray("resources");
            if (resources != null && resources.size() > 0) {
                JSONObject resource = (JSONObject) resources.get(0);
                String[] preAndId = resource.getString("url").split("/s/");
                res = new ResourceData(resource.getString("title"), preAndId[1]);
                break;
            }
            Integer id = d_.getInteger("id");
            String idType = switch (type) {
                case "tv" -> "tv_id";
                case "movies" -> "movie_id";
                default -> type + "_id";
            };
            res = get3(idType, id);
            break;
        }
        return res;
    }

    /**
     * 获得分享链接
     */
    private static ResourceData get3(String idType, Integer id) {
        Map<String, Object> map = Map.of(idType, id,
                "sort_by", "is_admin", "sort_order", "descend", "per_page", 100);
        String respJson = HttpRequest.get(url3)
                .bearerAuth(auth).form(map).execute()
                .body();
        JSONObject jsonObject = JSONObject.parseObject(respJson);
        Boolean success = jsonObject.getBoolean("success");
        //auth可能过期
        if (success == null || !success) return null;
        JSONArray datas = jsonObject.getJSONArray("data");
        //没有分享链接
        if (datas == null || datas.size() == 0) return null;
        ResourceData resourceData = null;
        for (Object data : datas) {
            JSONObject data_ = (JSONObject) data;
            String[] preAndId = data_.getString("url").split("/s/");
            resourceData = new ResourceData(data_.getString("title"), preAndId[1]);
            break;
        }
        return resourceData;
    }
}
