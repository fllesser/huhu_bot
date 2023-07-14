package tech.chowyijiu.huhu_bot.exception;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
public class NetworkError extends RuntimeException {

    public NetworkError(String message, Throwable cause) {
        super(message, cause);
    }
}
