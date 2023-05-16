package tech.chowyijiu.huhu_bot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.MessageResp;
import tech.chowyijiu.huhu_bot.entity.gocq.event.Event;

@SpringBootTest
class HuhuBotApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void test1() {
        MessageResp messageResp = new MessageResp();
        messageResp.setPostType(PostTypeEnum.notice.name());
        messageResp.setNoticeType(NoticeTypeEnum.notify.name());
        messageResp.setSubType(SubTypeEnum.poke.name());
        Event event = Event.respToEvent(messageResp);
        System.out.println(event.getClass().getSimpleName());
    }
}
