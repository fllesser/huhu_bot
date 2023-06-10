package tech.chowyijiu.huhu_bot.exception.gocq;

import lombok.NoArgsConstructor;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@NoArgsConstructor
public class NetworkError extends RuntimeException {

    public NetworkError(String message, Throwable cause) {
        super(message, cause);
    }
}
