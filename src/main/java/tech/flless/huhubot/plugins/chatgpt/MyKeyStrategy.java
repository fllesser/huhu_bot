package tech.flless.huhubot.plugins.chatgpt;

import cn.hutool.core.util.RandomUtil;
import com.unfbx.chatgpt.function.KeyStrategyFunction;

import java.util.List;

/**
 * @author elastic chow
 * @date 27/7/2023
 */
public class MyKeyStrategy implements KeyStrategyFunction<List<String>, String> {

    public static String curKey;

    @Override
    public String apply(List<String> apiKeys) {
        curKey = RandomUtil.randomEle(apiKeys);
        return curKey;
    }
}
