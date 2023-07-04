package tech.chowyijiu.huhu_bot.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author elastic chow
 * @date 8/6/2023
 */
public class StringUtil {

    public static boolean hasLength(String str) {
        return str != null && str.length() != 0;
    }

    public static void hasLength(String str, Consumer<String> consumer) {
        if (hasLength(str)) consumer.accept(str);
    }


    public static <R> R hasLengthReturn(String str, Function<String, R> function) {
        if (hasLength(str)) return function.apply(str);else return null;
    }

    public static String manMachine(String str) {
        return "[bot]" + str;
    }


    public String underlineToHump(String str){
        Pattern UNDERLINE_PATTERN = Pattern.compile("_([a-z])");
        //正则匹配下划线及后一个字符，删除下划线并将匹配的字符转成大写
        Matcher matcher = UNDERLINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配的子串替换成指定字符串，并且将替换后的子串及之前到上次匹配的子串之后的字符串添加到StringBuffer对象中
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            //把之后的字符串也添加到StringBuffer对象中
            matcher.appendTail(sb);
        } else {
            //去除除字母之外的前面带的下划线
            return sb.toString().replaceAll("_", "");
        }
        return underlineToHump(sb.toString());
    }

}
