package tech.flless.huhubot.controller;

import jakarta.annotation.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.flless.huhubot.adapters.onebot.v11.bot.Bot;
import tech.flless.huhubot.adapters.onebot.v11.bot.BotContainer;
import tech.flless.huhubot.adapters.onebot.v11.constant.OnebotAction;

import java.util.Arrays;
import java.util.Map;

/**
 * @author flless
 * @date 28/8/2023
 */
//@RestController
//@RequestMapping("/gocq")
@Deprecated
public class GocqController {

    //@RequestMapping(value = "/{qq}/{action}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> gocqAction(
            @PathVariable Long qq,
            @PathVariable String action,
            @RequestParam @Nullable Map<String, Object> params)
    {
        if (Arrays.stream(OnebotAction.values()).noneMatch(a -> a.name().equals(action))) {
            return ResponseEntity.ok("onebot实现未支持该api: " + action);
        }
        Bot bot = BotContainer.getBot(qq);
        if (bot == null) return ResponseEntity.ok("没有连接对应的bot, id:" + qq);
        OnebotAction onebotAction = OnebotAction.valueOf(action);
        String res;
        if (onebotAction.isHasResp()) {
            res = bot.callApiWaitResp(onebotAction, params);
        } else {
            bot.callApi(onebotAction, params);
            res = "该api没有响应数据";
        }
        return ResponseEntity.ok(res);
    }


}
