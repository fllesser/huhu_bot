package tech.flless.huhubot.core.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author elastic chow
 * @date 27/6/2023
 */
@Getter
@NoArgsConstructor
public class FinishedException extends RuntimeException {

    public FinishedException(String message) {
        super(message);
    }

}
