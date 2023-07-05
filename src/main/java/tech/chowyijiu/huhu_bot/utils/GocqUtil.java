package tech.chowyijiu.huhu_bot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tech.chowyijiu.huhu_bot.exception.gocq.ActionFailed;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
public class GocqUtil {
    private GocqUtil() {
    }

    public static final int poolSize;
    public static final long timeout = 5000L;
    public static final ExecutorService pool;

    private static final Map<String, LinkedBlockingDeque<String>> resultMap = new HashMap<>();

    static {
        poolSize = Runtime.getRuntime().availableProcessors() + 1;
        pool = new ThreadPoolExecutor(
                poolSize, poolSize * 2, 24L, TimeUnit.HOURS,
                new SynchronousQueue<>(),
                new CustomizableThreadFactory("wait-gocq-res-")
        );
    }

    public static void putEchoResult(String echo, String data) {
        if (resultMap.containsKey(echo)) resultMap.get(echo).offer(data);
    }

    /***
     * 等待响应
     * @param echo 回声
     * @return String
     */
    public static String waitResp(String echo) {
        if (!StringUtil.hasLength(echo)) log.info("echo is empty, ignored");
        log.info("FutureTask will be submitted, echo: {}", echo);
        //提交task等待gocq传回数据
        FutureTask<String> futureTask = new FutureTask<>(new Task(echo));
        resultMap.put(echo, new LinkedBlockingDeque<>(1));
        pool.submit(futureTask);
        try {
            return futureTask.get();
        } catch (InterruptedException e) {
            throw new ActionFailed("等待响应数据线程中断异常, echo:" + echo);
        } catch (ExecutionException e) {
            throw new ActionFailed("等待响应数据异常, echo:" + echo);
        } finally {
            futureTask.cancel(true);
            resultMap.remove(echo);
        }
    }

    private static class Task implements Callable<String> {
        private final String echo;

        Task(String echo) {
            this.echo = echo;
        }

        @Override
        public String call() throws Exception {
            return GocqUtil.resultMap.get(echo).poll(GocqUtil.timeout, TimeUnit.MILLISECONDS);
        }
    }

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