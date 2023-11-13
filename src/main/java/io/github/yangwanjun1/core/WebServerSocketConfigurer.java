package io.github.yangwanjun1.core;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
@EnableWebSocket
@Configuration
@ConditionalOnProperty(prefix = "opq",value = "enabled-reverse-ws",havingValue = "true" ,matchIfMissing = true)
public class WebServerSocketConfigurer implements WebSocketConfigurer {

    @Resource(name = "opqProperties")
    private OpqProperties properties;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        WsServerSocket.setPort(properties.getReversePort());
        registry.addHandler(new WsServerSocket(), properties.getReverseWs())
                .setAllowedOrigins("*");
    }
}
