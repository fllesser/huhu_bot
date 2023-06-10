package tech.chowyijiu.huhu_bot.exception.gocq;

import lombok.NoArgsConstructor;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@NoArgsConstructor
public class ActionFailed extends RuntimeException {

    public ActionFailed(String message) {
        super(message);
    }
}
