package tech.chowyijiu.huhu_bot.exception.gocq;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Slf4j
@NoArgsConstructor
public class NetworkError extends RuntimeException {

    public NetworkError(String message, Throwable cause) {
        super(message, cause);
        log.error(message, cause);
    }
}
