package tech.flless.huhubot.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tech.flless.huhubot.core.exception.FinishedException;
import tech.flless.huhubot.core.thread.ShareRunsPolicy;
import java.util.concurrent.*;

/**
 * 线程池工厂
 *
 * @author elastic chow
 * &#064;date  13/5/2023
 */
@Getter
@Slf4j
public class ThreadPoolUtil {
    private ThreadPoolUtil() {
    }

    public final static ThreadPoolExecutor ReechoExecutor;

    static {
        ReechoExecutor = new ThreadPoolExecutor(
                1, 2, 1, TimeUnit.HOURS,
                new ArrayBlockingQueue<>(4),
                new CustomizableThreadFactory("sync-reecho-"),
                (r, executor) -> {throw new FinishedException("同步队列溢出, 请求拒绝");}
        );
    }


}