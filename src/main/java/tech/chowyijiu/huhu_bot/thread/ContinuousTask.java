package tech.chowyijiu.huhu_bot.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * @author elastic chow
 * @date 21/6/2023
 */
public class ContinuousTask implements Runnable {

    private final String sessionToken;
    private final Long userId;
    private final Long groupId;
    private volatile String message;

    private static final Map<String, ContinuousTask> map = new HashMap<>();

    public static void execute(String sessionToken, Long userId, Long groupId) {
        map.put(sessionToken, new ContinuousTask(sessionToken, userId, groupId));
    }

    public ContinuousTask(String session, Long userId, Long groupId) {
        this.sessionToken = session;
        this.userId = userId;
        this.groupId = groupId;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        int count = 0;
        while (!Thread.currentThread().isInterrupted()) {
            if (this.message != null) {
                //zh
                count = 0;
            }

            if (count == 30) break;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;

        }
    }
}
