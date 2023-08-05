package tech.chowyijiu.huhubot.plugins.resource_search.cache_;

import java.util.ArrayList;
import java.util.List;

/**
 * @author elastic chow
 * @date 22/7/2023
 */
public class ResourceUtil {

    private static final List<ResourceData> resourceDataList = new ArrayList<>();

    public static void clear() {
        resourceDataList.clear();
    }

    public static void add(ResourceData data) {
        resourceDataList.add(data);
    }

    public static void addAll(List<ResourceData> dataList) {
        resourceDataList.addAll(dataList);
    }

    public static ResourceData get(int index) {
        return resourceDataList.get(index);
    }

    public static String getByKeyWord(String keyword) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < resourceDataList.size(); i++) {
            ResourceData data = resourceDataList.get(i);
            if (data.getName().contains(keyword)) {
                sb.append("\n[").append(i).append("]").append(data.getName()).append(" ")
                        .append("https://www.aliyundrive.com/s/").append(data.getShareId());
            }

        }
        return sb.toString();
    }

    //todo 移除关键词资源
    public static String buildString(List<ResourceData> dataList) {
        if (dataList == null || dataList.size() == 0) return "未搜索到此关键词资源, 试试别的";
        int start = resourceDataList.size();
        addAll(dataList);
        StringBuilder sb = new StringBuilder();
        sb.append("共搜索到").append(dataList.size()).append("个资源");
        for (int i = 0; i < dataList.size(); i++) {
            ResourceData data = dataList.get(i);
            sb.append("\n[").append(start + i).append("]").append(data.getName()).append(" ")
                    .append("https://www.aliyundrive.com/s/").append(data.getShareId());
        }
        return sb.toString();
    }
}
