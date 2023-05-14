package tech.chowyijiu.huhu_bot.utils;

/**
 * 线程池工厂
 *
 * @author elastic chow
 * @date 13/5/2023
 */

import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tech.chowyijiu.huhu_bot.thread.ShareRunsPolicy;

import java.util.concurrent.*;

@Slf4j
public class ThreadPoolUtil {
    private ThreadPoolUtil() {
    }

    private final static ThreadPoolExecutor handleCommandPool =
            new HandleCommandThreadPoolExecutor(5, 10, 1, TimeUnit.HOURS,
                    new ArrayBlockingQueue<>(20),
                    new CustomizableThreadFactory("pool-handleCommand-"),
                    new ShareRunsPolicy("pool-handleCommand"));
    private final static ExecutorService sharePool =
            new ThreadPoolExecutor(1, 1, 3L * 1000L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(),
                    new CustomizableThreadFactory("pool-share-")
            );

    static {
        resetThreadPoolSize();
    }

    public static class HandleCommandThreadPoolExecutor extends ThreadPoolExecutor {

        public HandleCommandThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue, @NotNull ThreadFactory threadFactory, @NotNull RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        public HandleCommandThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue, @NotNull RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        }

        public HandleCommandThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue, @NotNull ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        public HandleCommandThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }


        @Override
        public void execute(@NotNull Runnable command) {
            super.execute(command);
            log.info("线程池已受理一个命令:{}", command);
        }

    }

    public static void resetThreadPoolSize() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (availableProcessors > 0) {
            handleCommandPool.setCorePoolSize(availableProcessors + 1);
            handleCommandPool.setMaximumPoolSize(availableProcessors * 2);

            log.info("根据cpu线程数:{}, 重置命令处理线程池容量完成", availableProcessors);
        }
    }


    public static Executor getHandleCommandPool() {
        return handleCommandPool;
    }

    public static ExecutorService getSharePool() {
        return sharePool;
    }
}