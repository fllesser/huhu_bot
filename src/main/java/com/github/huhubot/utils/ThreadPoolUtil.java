package com.github.huhubot.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import com.github.huhubot.core.exception.FinishedException;

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

    public final static ThreadPoolExecutor ProcessEventExecutor;

    static {
        ReechoExecutor = new ThreadPoolExecutor(
                2, 2, 1, TimeUnit.HOURS,
                new ArrayBlockingQueue<>(1),
                new CustomizableThreadFactory("sync-reecho-"),
                (r, executor) -> {throw new FinishedException("同步队列溢出, 请求拒绝");});
        ProcessEventExecutor = new ThreadPoolExecutor(
                5, 10, 1, TimeUnit.HOURS,
                new ArrayBlockingQueue<>(10),
                new CustomizableThreadFactory("process-event-"),
                (r, executor) -> log.error("Event processing thread pool exceeds load, task ignored, event[{}]", r.toString()));

    }


}