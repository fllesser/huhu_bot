package tech.chowyijiu.huhu_bot;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tech.chowyijiu.huhu_bot.entity.message.Message;
import tech.chowyijiu.huhu_bot.entity.message.MessageSegment;
import tech.chowyijiu.huhu_bot.plugins.vedioResource.gitcafe.Data;
import tech.chowyijiu.huhu_bot.plugins.vedioResource.gitcafe.GitcafeResp;
import tech.chowyijiu.huhu_bot.plugins.fortnite.FortniteApi;
import tech.chowyijiu.huhu_bot.plugins.fortnite.ShopEntry;
import tech.chowyijiu.huhu_bot.utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@SuppressWarnings("all")
public class HuhuBotApplicationTests {

    @Test
    void contextLoads() {
    }

    //@Test
    public void test1() {
        String privateMessageJson = "{\"post_type\":\"message\",\"message_type\":\"private\",\"time\":1684227772,\"self_id\":1487248817,\"sub_type\":\"friend\",\"raw_message\":\"。。\",\"font\":0,\"sender\":{\"age\":0,\"nickname\":\"Oswald Kan Mon May 15 2023 CST\",\"sex\":\"unknown\",\"user_id\":1942422015},\"message_id\":-2100915033,\"user_id\":1942422015,\"target_id\":1487248817,\"message\":\"。。\"}";
        String groupMessageJson = "{\"post_type\":\"message\",\"message_type\":\"group\",\"time\":1684228450,\"self_id\":1487248817,\"sub_type\":\"normal\",\"anonymous\":null,\"font\":0,\"message_seq\":6181,\"raw_message\":\"群消息\",\"sender\":{\"age\":0,\"area\":\"\",\"card\":\"ID：Jarid Harris\",\"level\":\"\",\"nickname\":\"Oswald Kan Mon May 15 2023 CST\",\"role\":\"owner\",\"sex\":\"unknown\",\"title\":\"\",\"user_id\":1942422015},\"user_id\":1942422015,\"group_id\":669026253,\"message\":\"群消息\",\"message_id\":-2058339141}";
    }

    //@Test
    public void testRegex() {
        String cq = "[CQ:at,qq=1487248817,file=https://dsfsdfsfsd,image=sdfsdfs.png]sdfsdf[CQ:file,qq=1487817,file=https://dsfsdfsfsd]";
        String regex = "\\[CQ:([a-z]+)((,([a-z]+)=([\\w:/.]+))+)]";
    }

    // @Test
    public void testMessage() {
        String cq1 = "测试[CQ:image,file=http://baidu.com/1.jpg]";
        String cq2 = "[CQ:image,file=http://baidu.com/1.jpg]";
        String cq3 = "[CQ:image,file=http://baidu.com/1.jpg]测试";
        String cq4 = "测试[CQ:image,file=https://baidu.com/1.jpg]sfdsaf[CQ:face,id=123]sdfsdf";
        Message message1 = Message.build(cq4);
        System.out.println(message1);
        System.out.println(message1.toArrayString());
        Message message2 = new Message();
        message2.add(MessageSegment.at(1942422015L));
        message2.add("测试");
        message2.add(MessageSegment.image("https://baidu.com/1.jpg", 0));
        System.out.println(message2);
    }

    //@Test
    public void testImageUtil() throws IOException {
        List<String> smallIcons = new ArrayList<>();
        for (ShopEntry shopEntry : FortniteApi.getShopEntries()) {
            for (ShopEntry.Item item : shopEntry.getItems()) {
                smallIcons.add(item.getImages().getSmallIcon());
            }
        }
        BufferedImage[] imgs = smallIcons.stream().map(url -> {
            InputStream inputStream = HttpRequest.get(url).execute().bodyStream();
            try {
                return ImageIO.read(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).toArray(BufferedImage[]::new);
        ImageUtil.mergeImage("/Users/yijiuchow/Desktop/1.png", imgs);
    }

    public void test() {
        Map<String, Object> map = new HashMap<>();
        map.put("action", "search");
        map.put("from", "web");
        map.put("token", "14730e6c54e9bead73a1299c08896026e1c3e");
        map.put("keyword", "消失的十一层");
        String resp = HttpUtil.post("https://gitcafe.net/tool/alipaper/", map);
        GitcafeResp gitcafeResp = JSONObject.parseObject(resp, GitcafeResp.class);
        //if (!gitcafeResp.getSuccess()) event.finish("查询失败" + gitcafeResp.getError());
        StringBuilder sb = new StringBuilder();
        List<Data> dataList = gitcafeResp.getData();
        sb.append("共查询到").append(dataList.size()).append("个资源");
        for (Data data : dataList) {
            sb.append("\n")
                    .append(data.getAlititle())
                    .append(" https://www.aliyundrive.com/s/").append(data.getAlikey());
        }
        System.out.println(sb);
    }




}
