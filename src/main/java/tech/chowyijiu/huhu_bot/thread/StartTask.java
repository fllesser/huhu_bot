package tech.chowyijiu.huhu_bot.thread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
//@Component
@RequiredArgsConstructor
public class StartTask implements Runnable {


    //private final SystemService systemService;

    @Override
    public void run() {
        try {
            //systemService.loadCache();
            // 创建stop脚本
            //systemService.writeStopScript();
        } catch (Exception e) {
            log.error("初始任务执行异常", e);
        }
    }

    public synchronized void execute(StartTask self) {
        new Thread(self).start();
    }
}