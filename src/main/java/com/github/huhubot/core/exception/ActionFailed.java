package com.github.huhubot.core.exception;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
public class ActionFailed extends RuntimeException {
    public ActionFailed(String message) {
        super("ActionFailed: \n" + message);
    }
}
