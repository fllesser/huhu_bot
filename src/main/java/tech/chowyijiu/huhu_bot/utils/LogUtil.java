package tech.chowyijiu.huhu_bot.utils;

import tech.chowyijiu.huhu_bot.constant.ANSI;

/**
 * @author elastic chow
 * @date 16/6/2023
 */
public class LogUtil {

    public static Object[] buildArgsWithColor(String color, Object... args) {
        int len = args.length + 2;
        Object[] result = new Object[len];
        result[0] = color;
        System.arraycopy(args, 0, result, 1, args.length);
        result[len - 1] = ANSI.RESET;
        return result;
    }
}
