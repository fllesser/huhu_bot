package tech.flless.huhubot.core.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
@SuppressWarnings("all")
public class ShareRunsPolicy implements RejectedExecutionHandler {
    private final String poolName;
    public ShareRunsPolicy(String poolName){
        this.poolName = poolName;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        if (!executor.isShutdown()) {
            log.info("线程池：{} 执行拒绝策略, task:{}", poolName, r);
        }

    }

}