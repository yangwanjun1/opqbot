package ywj.gz.cn.core.ws;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import ywj.gz.cn.config.QQConfigProperties;
import ywj.gz.cn.core.BotThreadPoll;
import ywj.gz.cn.core.CacheImage;
import ywj.gz.cn.core.CompressImage;

import java.util.Map;

@EnableWebSocket
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "bot",value = "enabled-reverse-ws",havingValue = "true" ,matchIfMissing = true)
public class WebServerSocketConfigurer implements WebSocketConfigurer {

    @Resource(name = "qqConfigProperties")
    private QQConfigProperties properties;
    @Autowired
    private ApplicationContext context;
    @Resource(name = "botThreadPoll")
    private BotThreadPoll botThreadPoll;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String ws = properties.getReverseWs();
        if (ws.startsWith("ws://")){
            log.error("reverse-ws 只能是路径");
            System.exit(-1);
        }
        Map<String, CompressImage> ofType = context.getBeansOfType(CompressImage.class);
        Map<String, CacheImage> cacheImageMap = context.getBeansOfType(CacheImage.class);
        CompressImage compressImage = ofType.isEmpty() ? null : ofType.entrySet().iterator().next().getValue();
        CacheImage cacheImage = cacheImageMap.isEmpty() ? null : cacheImageMap.entrySet().iterator().next().getValue();
        registry.addHandler(new WsServerSocket(properties,botThreadPoll,compressImage,cacheImage), ws).setAllowedOrigins("*");
    }
}
