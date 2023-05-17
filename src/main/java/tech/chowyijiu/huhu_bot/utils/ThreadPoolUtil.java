package tech.chowyijiu.huhu_bot.utils;

/**
 * 线程池工厂
 *
 * @author elastic chow
 * @date 13/5/2023
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tech.chowyijiu.huhu_bot.thread.ShareRunsPolicy;

import java.util.concurrent.*;

@Slf4j
public class ThreadPoolUtil {
    private ThreadPoolUtil() {
    }

    private final static ThreadPoolExecutor executor =
            new ProcessEventThreadPoolExecutor(5, 10, 1, TimeUnit.HOURS,
                    new ArrayBlockingQueue<>(20),
                    new CustomizableThreadFactory("pool-process-event"),
                    new ShareRunsPolicy("pool-process-event"));

    static {
        resetThreadPoolSize();
    }

    public static class ProcessEventThreadPoolExecutor extends ThreadPoolExecutor {

        public ProcessEventThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        @Override
        public void execute(Runnable task) {
            log.info("The thread pool has accepted a {}", task.getClass().getSimpleName());
            super.execute(task);
        }

    }

    public static void resetThreadPoolSize() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (availableProcessors > 0) {
            executor.setCorePoolSize(availableProcessors * 2);
            executor.setMaximumPoolSize(availableProcessors * 4);
            log.info("根据CPU线程数:{}, 重置事件处理线程池容量完成 corePoolSize:[{}], maximumPoolSize:[{}]",
                    availableProcessors, executor.getCorePoolSize(), executor.getMaximumPoolSize());
        }
    }

    public static ThreadPoolExecutor getExecutor() {
        return executor;
    }

}