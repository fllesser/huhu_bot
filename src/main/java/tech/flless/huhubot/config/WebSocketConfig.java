package tech.flless.huhubot.config;


import jakarta.servlet.ServletContext;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.util.WebAppRootListener;
import tech.flless.huhubot.adapters.onebot.v11.bot.OneBotV11Handler;


/**
 * @author elastic chow
 * @date 14/5/2023
 */
@EnableWebSocket //开启websocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer, ServletContextInitializer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new OneBotV11Handler(),"/onebot/v11/ws").setAllowedOrigins("*");
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        servletContext.addListener(WebAppRootListener.class);
        //指定buffersize，若收到得数据大于设置值，会导致连接中断
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize","819200");
        //servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize","16384");
    }


}