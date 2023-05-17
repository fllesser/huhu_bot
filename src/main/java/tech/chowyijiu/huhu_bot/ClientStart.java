package tech.chowyijiu.huhu_bot;

import tech.chowyijiu.huhu_bot.ws.Client;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
//@Component
public class ClientStart /*implements CommandLineRunner*/ {


    //@Override
    public void run(String... args) throws Exception {
        boolean success = Client.connect("ws://192.168.5.135:8888");
    }
}
