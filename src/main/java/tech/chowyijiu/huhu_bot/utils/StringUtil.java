package tech.chowyijiu.huhu_bot.utils;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author elastic chow
 * @date 8/6/2023
 */
public class StringUtil {

    public static boolean hasLength(String str) {
        return str != null && str.length() > 0;
    }

    public static void hasLength(String str, Consumer<String> consumer) {
        if (hasLength(str)) consumer.accept(str);
    }


    public static <R> R hasLengthReturn(String str, Function<String, R> function) {
        if (hasLength(str)) return function.apply(str);
        else return null;
    }
}
