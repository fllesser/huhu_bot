package tech.chowyijiu.huhubot.utils;

/**
 * 线程池工厂
 *
 * @author elastic chow
 * @date 13/5/2023
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tech.chowyijiu.huhubot.constant.ANSI;
import tech.chowyijiu.huhubot.thread.ShareRunsPolicy;

import java.util.concurrent.*;

@Slf4j
public class ThreadPoolUtil {
    private ThreadPoolUtil() {
    }

    private final static ThreadPoolExecutor eventExecutor;

    static {
        int corePoolSize = Runtime.getRuntime().availableProcessors() + 1;
        assert corePoolSize > 0;
        eventExecutor = new ProcessEventThreadPoolExecutor(corePoolSize, corePoolSize * 2,
                1, TimeUnit.HOURS,
                new ArrayBlockingQueue<>(corePoolSize * 4),
                new CustomizableThreadFactory("process-event-"),
                new ShareRunsPolicy("EventExecutor"));
        log.info("{}根据CPU线程数:{}, 创建事件处理线程池 corePoolSize:[{}], maximumPoolSize:[{}]{}",
                ANSI.YELLOW, corePoolSize - 1, eventExecutor.getCorePoolSize(),
                eventExecutor.getMaximumPoolSize(), ANSI.RESET);
    }

    public static class ProcessEventThreadPoolExecutor extends ThreadPoolExecutor {

        public ProcessEventThreadPoolExecutor(
                int corePoolSize, int maximumPoolSize, long keepAliveTime,
                TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        @Override
        public void execute(Runnable task) {
            //log.info("[ThreadPool] Accepted a {}", task.getClass().getSimpleName());
            super.execute(task);
        }

    }

    public static ThreadPoolExecutor getEventExecutor() {
        return eventExecutor;
    }

}