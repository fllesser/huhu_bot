package tech.chowyijiu.huhu_bot.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
public class ShareRunsPolicy implements RejectedExecutionHandler {
    private final String poolName;
    public ShareRunsPolicy(String poolName){
        this.poolName = poolName;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            //ThreadPoolUtil.getSharePool().execute(r);
            //log.info("线程池：{} 执行拒绝策略，本次任务由公共线程池执行, executor: {},",poolName, executor);
            log.info("线程池：{} 执行拒绝策略", poolName);
        }

    }
}