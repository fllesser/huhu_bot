package tech.chowyijiu.huhubot.controller;

import jakarta.annotation.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.chowyijiu.huhubot.adapters.onebot.v11.bot.OneBotV11Handler;
import tech.chowyijiu.huhubot.core.constant.GocqAction;
import tech.chowyijiu.huhubot.adapters.onebot.v11.bot.Bot;

import java.util.Arrays;
import java.util.Map;

/**
 * @author flless
 * @date 28/8/2023
 */
@RestController
@RequestMapping("/gocq")
public class GocqController {

    @RequestMapping(value = "/{qq}/{action}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> gocqAction(
            @PathVariable Long qq,
            @PathVariable String action,
            @RequestParam @Nullable Map<String, Object> params)
    {
        if (Arrays.stream(GocqAction.values()).noneMatch(a -> a.name().equals(action))) {
            return ResponseEntity.ok("gocq未支持该api: " + action);
        }
        Bot bot = OneBotV11Handler.getBot(qq);
        if (bot == null) return ResponseEntity.ok("没有连接对应的bot, id:" + qq);
        GocqAction gocqAction = GocqAction.valueOf(action);
        String res;
        if (gocqAction.isHasResp()) {
            res = bot.callApiWaitResp(gocqAction, params);
        } else {
            bot.callApi(gocqAction, params);
            res = "该api没有响应数据";
        }
        return ResponseEntity.ok(res);
    }


}
