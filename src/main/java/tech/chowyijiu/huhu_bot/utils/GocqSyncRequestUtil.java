package tech.chowyijiu.huhu_bot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tech.chowyijiu.huhu_bot.exception.gocq.ActionFailed;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
public class GocqSyncRequestUtil {
    private GocqSyncRequestUtil() {
    }

    public static int poolSize = Runtime.getRuntime().availableProcessors() + 1;
    public static long sleep = 1000L;
    public static final ExecutorService pool =
            new ThreadPoolExecutor(poolSize, poolSize * 2, 24L, TimeUnit.HOURS,
                    new SynchronousQueue<>(), new CustomizableThreadFactory("pool-sendSyncMessage-"));

    private static final Map<String, String> resultMap = new ConcurrentHashMap<>();

    public static void putEchoResult(String key, String val) {
        resultMap.put(key, val);
    }

    /***
     * 发送同步消息
     * @param echo echo 回声
     * @param timeout 超时 ms
     * @return String
     */
    public static String sendSyncRequest(String echo, long timeout) {
        log.info("futureTask echo: {}", echo);
        //提交task等待gocq传回数据
        FutureTask<String> futureTask = new FutureTask<>(new Task(echo));
        pool.submit(futureTask);
        try {
            String res;
            if (timeout <= sleep) {
                res = futureTask.get();
            } else {
                res = futureTask.get(timeout, TimeUnit.MILLISECONDS);
            }
            //log.info("echo: {}, result: {}", echo, res);
            return res;
        } catch (InterruptedException e) {
            throw new ActionFailed("发送同步消息线程中断异常, echo:" + echo);
        } catch (ExecutionException e) {
            throw new ActionFailed("发送同步消息执行异常, echo:" + echo);
        } catch (TimeoutException e) {
            throw new ActionFailed("发送同步消息超时, 或者该api无响应数据, echo:" + echo);
        } catch (Exception e) {
            throw new ActionFailed("发送同步消息发生未知异常, echo:" + echo + ", Exception:" + e.getMessage());
        } finally {
            futureTask.cancel(true);
            //这里似乎不一定能删掉
            resultMap.remove(echo);
        }
    }

    private static class Task implements Callable<String> {
        private final String echo;

        Task(String echo) {
            if (!StringUtil.hasLength(echo)) {
                throw new IllegalArgumentException("echo is blank");
            }
            this.echo = echo;
        }

        @SuppressWarnings("BusyWait")
        @Override
        public String call() throws Exception {
            String res;
            do {
                res = GocqSyncRequestUtil.resultMap.get(echo);
                if (res != null) break;
                else Thread.sleep(GocqSyncRequestUtil.sleep);
            } while (!Thread.currentThread().isInterrupted());
            return res;
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
    //    String responseStr = sendSyncRequest(bot, GocqActionEnum.UPLOAD_PRIVATE_FILE, param, timeout);
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
    //    String jsonStr = sendSyncRequest(bot, GocqActionEnum.DOWNLOAD_FILE, param, timeout);
    //    if (jsonStr != null) {
    //        return JSONObject.parseObject(jsonStr, DownloadFileResp.class);
    //    }
    //    return null;
    //}
}