package tech.chowyijiu.huhu_bot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tech.chowyijiu.huhu_bot.constant.CqTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.message.MessageSegment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class HuhuBotApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void test1() {
        String privateMessageJson = "{\"post_type\":\"message\",\"message_type\":\"private\",\"time\":1684227772,\"self_id\":1487248817,\"sub_type\":\"friend\",\"raw_message\":\"。。\",\"font\":0,\"sender\":{\"age\":0,\"nickname\":\"Oswald Kan Mon May 15 2023 CST\",\"sex\":\"unknown\",\"user_id\":1942422015},\"message_id\":-2100915033,\"user_id\":1942422015,\"target_id\":1487248817,\"message\":\"。。\"}";
        String groupMessageJson = "{\"post_type\":\"message\",\"message_type\":\"group\",\"time\":1684228450,\"self_id\":1487248817,\"sub_type\":\"normal\",\"anonymous\":null,\"font\":0,\"message_seq\":6181,\"raw_message\":\"群消息\",\"sender\":{\"age\":0,\"area\":\"\",\"card\":\"ID：Jarid Harris\",\"level\":\"\",\"nickname\":\"Oswald Kan Mon May 15 2023 CST\",\"role\":\"owner\",\"sex\":\"unknown\",\"title\":\"\",\"user_id\":1942422015},\"user_id\":1942422015,\"group_id\":669026253,\"message\":\"群消息\",\"message_id\":-2058339141}";
    }

    @Test
    public void testRegex() {
        String cq = "[CQ:at,qq=1487248817,file=https://dsfsdfsfsd,image=sdfsdfs.png]sdfsdf[CQ:file,qq=1487817,file=https://dsfsdfsfsd]";
        String regex = "\\[CQ:([a-z]+)((,([a-z]+)=([\\w:/.]+))+)]";
        Pattern r = Pattern.compile(regex);
        Matcher matcher = r.matcher(cq);
        if (matcher.find()) {
            String type = matcher.group(1);
            MessageSegment.CqCode cqCode = new MessageSegment.CqCode(CqTypeEnum.valueOf(type));
            String[] split = matcher.group(2).replaceFirst(",", "").split(",");
            for (String s : split) {
                String[] keyValue = s.split("=");
                cqCode.addParam(keyValue[0], keyValue[1]);
            }
            System.out.println(cqCode);
        }

    }

}
