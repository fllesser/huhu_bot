//package tech.chowyijiu.huhubot.plugins.fortnite;
//
//import cn.hutool.http.HttpRequest;
//import cn.hutool.http.HttpResponse;
//import lombok.extern.slf4j.Slf4j;
//import tech.chowyijiu.huhubot.core.annotation.MessageHandler;
//import tech.chowyijiu.huhubot.core.entity.arr_message.ForwardMessage;
//import tech.chowyijiu.huhubot.core.entity.arr_message.Message;
//import tech.chowyijiu.huhubot.core.entity.arr_message.MessageSegment;
//import tech.chowyijiu.huhubot.core.event.message.GroupMessageEvent;
//import tech.chowyijiu.huhubot.core.ws.Bot;
//import tech.chowyijiu.huhubot.core.ws.Huhubot;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Arrays;
//import java.util.LinkedHashMap;
//import java.util.List;
//
///**
// * @author elastic chow
// * @date 14/5/2023
// */
//@Slf4j
////@BotPlugin(name = "堡垒之夜")
//@SuppressWarnings("unused")
//public class FortnitePlugin {
//
//    private final String shopUrl = "https://cdn.dingpanbao.cn/blzy/shop.png";
//    private final String shopPath = "/home/chow/shop.png";
//    private final String shop = MessageSegment.image(shopUrl) + "";
//    private volatile Long lastUpdateTime = 0L;
//
//    private List<ForwardMessage> nodes;
//    private final Long[] groups = {754044548L, 208248400L};
//
//    //@Scheduled(cron = "30 0 8 * * * ")
//    public void scheduleShop() throws InterruptedException {
//        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
//        map.put("微信小程序搜索堡垒皮肤", shop);
//        List<ShopEntry> shopEntries = FortniteApi.getShopEntries();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        String formatTime = "更新时间: " +  LocalDateTime.now().format(formatter);
//        map.put(formatTime,  "下面为详细商城, 共有" + shopEntries.size() + "个物品");
//        for (ShopEntry shopEntry : shopEntries) {
//            ShopEntry.Buddle bundle = shopEntry.getBundle();
//            String name;
//            String imageUrl;
//            if (bundle != null) {
//                name = bundle.getName();
//                imageUrl = bundle.getImage();
//            } else {
//                ShopEntry.Item item = shopEntry.getItems()[0];
//                imageUrl = item.getImages().getIcon();
//                name = item.getName();
//            }
//            Message message = new Message()
//                    .append(name + " " + shopEntry.getFinalPrice() + "VB")
//                    .append(MessageSegment.image(imageUrl));
//            map.put(name, message);
//        }
//        nodes = ForwardMessage.quickBuild(501273515L, map);
//        for (Bot bot : Huhubot.getBots()) {
//            bot.sendGroupForwardMsg(754044548L, nodes);
//            Thread.sleep(1000 * 10L);
//            bot.sendGroupForwardMsg(208248400L, nodes);
//        }
//    }
//
//    //@MessageHandler(name = "商城1", commands = "shop", priority = 1, block = true, rule = RuleEnum.superuser)
//    public void myshop(Bot bot, GroupMessageEvent event) throws InterruptedException {
//        if (nodes == null) {
//            scheduleShop();
//        }
//        if (Arrays.stream(groups).toList().contains(event.getGroupId())) {
//            bot.sendGroupForwardMsg(event.getGroupId(), nodes);
//        }
//    }
//
//    @MessageHandler(name = "商城2", commands = {"商城","shop"}, priority = 0, block = true)
//    public void shop(Bot bot, GroupMessageEvent event) {
//        //bot.sendMessage(event, "今日商城" + shop, false);
//        updateShopImage();
//        bot.sendMessage(event, MessageSegment.image("file://" + shopPath));
//    }
//
//
//    private void updateShopImage() {
//        long now = System.currentTimeMillis();
//        if (now - lastUpdateTime >= 86400) {
//            lastUpdateTime = now; //直接修改, 其他线程可见它的最新值
//            HttpResponse resp = HttpRequest.get(shopUrl).execute();
//            resp.sync().writeBody(shopPath);
//        }
//    }
//
//}
