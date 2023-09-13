package tech.chowyijiu.huhubot.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
public class GocqUtil {

    private GocqUtil() {
    }

    //public static final long timeout = 5000L;
    //
    //private static final Map<String, LinkedBlockingDeque<String>> respMap = new HashMap<>();
    //
    //public static void putEchoResult(String echo, String data) {
    //    if (respMap.containsKey(echo)) respMap.get(echo).offer(data);
    //}
    //
    ///***
    // * 等待响应
    // * @param echo 回声
    // * @return String
    // */
    //public static String waitResp(String echo) {
    //    if (!StringUtil.hasLength(echo)) log.info("echo is empty, ignored");
    //    log.info("Blocking waits for gocq to return the result, echo: {}", echo);
    //    LinkedBlockingDeque<String> blockingRes = new LinkedBlockingDeque<>(1);
    //    respMap.put(echo, blockingRes);
    //    try {
    //        return blockingRes.poll(GocqUtil.timeout, TimeUnit.MILLISECONDS);
    //    } catch (InterruptedException e) {
    //        throw new ActionFailed("等待响应数据线程中断异常, echo:" + echo);
    //    } finally {
    //        respMap.remove(echo);
    //    }
    //}

    /**
     * 发送私聊文件
     *
     * @param userId   userId
     * @param filePath 该文件必须与go-cqhttp在同一主机上
     * @param fileName 文件名
     * @param timeout  timeout
     * @return SyncResponse
     */
    //@Deprecated
    //public static SyncResponse uploadPrivateFile(
    //        Bot bot, Long userId, String filePath, String fileName, long timeout) {
    //    Map<String, Object> param = new HashMap<>(3);
    //    param.put("user_id", userId);
    //    param.put("file", filePath);
    //    param.put("name", fileName);
    //    String responseStr = waitResp(bot, GocqActionEnum.UPLOAD_PRIVATE_FILE, param, timeout);
    //    if (responseStr != null) {
    //        return JSONObject.parseObject(responseStr, SyncResponse.class);
    //
    //    }
    //    return null;
    //}

    /**
     * 用gocq去下载文件
     *
     * @param url         url
     * @param threadCount 下载线程数
     * @param httpHeaders http头
     * @param timeout     timeout
     * @return 返回gocq下载到的文件绝对路径
     */
    //@Deprecated
    //public static DownloadFileResp downloadFile(
    //        Bot bot, String url, int threadCount, HttpHeaders httpHeaders, long timeout) {
    //
    //    Map<String, Object> param = new HashMap<>(3);
    //    param.put("url", url);
    //    param.put("thread_count", threadCount);
    //
    //    if (httpHeaders != null && !httpHeaders.isEmpty()) {
    //        List<String> headStrs = new ArrayList<>();
    //        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
    //            StringBuilder item = new StringBuilder(entry.getKey() + "=");
    //            for (String s : entry.getValue()) {
    //                item.append(s).append(";");
    //            }
    //            headStrs.add(item.toString());
    //        }
    //        param.put("headers", JSONObject.toJSONString(headStrs));
    //
    //    }
    //    String jsonStr = waitResp(bot, GocqActionEnum.DOWNLOAD_FILE, param, timeout);
    //    if (jsonStr != null) {
    //        return JSONObject.parseObject(jsonStr, DownloadFileResp.class);
    //    }
    //    return null;
    //}
}