package tech.chowyijiu.huhu_bot.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.util.WebAppRootListener;
import tech.chowyijiu.huhu_bot.ws.Server;

import javax.servlet.ServletContext;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@EnableWebSocket //开启websocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer, ServletContextInitializer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new Server(),"/ws").setAllowedOrigins("*");
    }


    @Override
    public void onStartup(ServletContext servletContext) {
        servletContext.addListener(WebAppRootListener.class);
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize","81920");
        //servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize","16384");
    }
}