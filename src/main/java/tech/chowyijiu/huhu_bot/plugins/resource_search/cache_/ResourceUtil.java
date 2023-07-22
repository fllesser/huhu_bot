package tech.chowyijiu.huhu_bot.plugins.resource_search.cache_;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author elastic chow
 * @date 22/7/2023
 */
public class ResourceUtil {

    private static final Map<String, List<ResourceData>> shareMap = new HashMap<>();

    public static void clear() {
        shareMap.clear();
    }

    public static void put(String keyword, ResourceData data) {
        List<ResourceData> dataList = shareMap.get(keyword);
        if (dataList != null) dataList.add(data);
    }

    public static void put(String keyword, List<ResourceData> dataList) {
        shareMap.put(keyword, dataList);
    }

    public static ResourceData get(String keyword, Integer index) {
        List<ResourceData> dataList = shareMap.get(keyword);
        if (dataList != null && dataList.size() > index) return dataList.get(index);
        return null;
    }

    public static String buildString(List<ResourceData> dataList) {
        if (dataList == null || dataList.size() == 0) return "未搜索到此关键词资源, 试试别的";
        StringBuilder sb = new StringBuilder();
        sb.append("共搜索到").append(dataList.size()).append("个资源\n");
        for (int i = 0; i < dataList.size(); i++) {
            ResourceData data = dataList.get(i);
            sb.append("[").append(i + 1).append("]").append(data.getName()).append(" ")
                    .append("https://www.aliyundrive.com/s/").append(data.getShareId());
        }
        return sb.toString();
    }
}
