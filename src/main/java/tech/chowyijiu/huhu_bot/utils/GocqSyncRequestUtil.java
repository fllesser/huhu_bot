package tech.chowyijiu.huhu_bot.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tech.chowyijiu.huhu_bot.constant.GocqActionEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.request.RequestBox;
import tech.chowyijiu.huhu_bot.entity.gocq.response.DownloadFileResp;
import tech.chowyijiu.huhu_bot.entity.gocq.response.SyncResponse;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.util.*;
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
    public static long sleep = 3000L;
    public static final ExecutorService pool =
            new ThreadPoolExecutor(poolSize, poolSize * 2, 24L, TimeUnit.HOURS,
                    new SynchronousQueue<>(), new CustomizableThreadFactory("pool-sendSyncMessage-"));

    private static final Map<String, String> resultMap = new ConcurrentHashMap<>();

    public static void putEchoResult(String key, String val) {
        resultMap.put(key, val);
    }

    /***
     * 发送同步消息
     * @param action 终结点
     * @param params 参数
     * @param timeout 超时 ms
     * @param <T> T
     * @return String
     */
    public static <T> String sendSyncRequest(
            Bot bot, GocqActionEnum action, T params, long timeout) {
        RequestBox<T> requestBox = new RequestBox<>();
        Optional.ofNullable(params).ifPresent(requestBox::setParams);
        requestBox.setAction(action.getAction());
        String echo = Thread.currentThread().getName() + "_" +
                bot.getUserId() + "_" +
                action.getAction() + "_" +
                UUID.randomUUID().toString().replace("-", "");
        requestBox.setEcho(echo);
        //发送请求
        bot.sessionSend(JSONObject.toJSONString(requestBox));
        //log.info("futureTask echo: {}", echo);
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
            log.error("发送同步消息线程中断异常, echo:{}", echo, e);
        } catch (ExecutionException e) {
            log.error("发送同步消息执行异常,echo:{}", echo, e);
        } catch (TimeoutException e) {
            log.error("发送同步消息超时,echo:{}", echo, e);
        } catch (Exception e) {
            log.error("发送同步消息异常,echo:{}", echo, e);
        } finally {
            futureTask.cancel(true);
            resultMap.remove(echo);
        }
        return null;
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
            String res = null;
            while (!Thread.currentThread().isInterrupted()) {
                res = resultMap.get(echo);
                if (res != null) {
                    break;
                } else {
                    Thread.sleep(GocqSyncRequestUtil.sleep);
                }
            }
            return res;
        }
    }

    /**
     * 发送私聊文件
     *
     * @param userId userId
     * @param filePath 该文件必须与go-cqhttp在同一主机上
     * @param fileName 文件名
     * @param timeout timeout
     * @return SyncResponse
     */
    @Deprecated
    public static SyncResponse uploadPrivateFile(
            Bot bot, Long userId, String filePath, String fileName, long timeout) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("user_id", userId);
        param.put("file", filePath);
        param.put("name", fileName);
        String responseStr = sendSyncRequest(bot, GocqActionEnum.UPLOAD_PRIVATE_FILE, param, timeout);
        if (responseStr != null) {
            return JSONObject.parseObject(responseStr, SyncResponse.class);

        }
        return null;
    }

    /**
     * 用gocq去下载文件
     *
     * @param url url
     * @param threadCount 下载线程数
     * @param httpHeaders http头
     * @param timeout timeout
     * @return 返回gocq下载到的文件绝对路径
     */
    @Deprecated
    public static DownloadFileResp downloadFile(
            Bot bot, String url, int threadCount, HttpHeaders httpHeaders, long timeout) {

        Map<String, Object> param = new HashMap<>(3);
        param.put("url", url);
        param.put("thread_count", threadCount);

        if (httpHeaders != null && !httpHeaders.isEmpty()) {
            List<String> headStrs = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
                StringBuilder item = new StringBuilder(entry.getKey() + "=");
                for (String s : entry.getValue()) {
                    item.append(s).append(";");
                }
                headStrs.add(item.toString());
            }
            param.put("headers", JSONObject.toJSONString(headStrs));

        }
        String jsonStr = sendSyncRequest(bot, GocqActionEnum.DOWNLOAD_FILE, param, timeout);
        if (jsonStr != null) {
            return JSONObject.parseObject(jsonStr, DownloadFileResp.class);
        }
        return null;
    }
}