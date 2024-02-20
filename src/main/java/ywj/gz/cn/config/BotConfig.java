package ywj.gz.cn.config;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import ywj.gz.cn.core.BotManager;
import ywj.gz.cn.core.BotThreadPoll;
import ywj.gz.cn.core.CacheImage;
import ywj.gz.cn.core.CompressImage;
import ywj.gz.cn.core.ws.WsSocketClient;
import ywj.gz.cn.core.handler.QQEventHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Configuration
public class BotConfig {
    @Resource(name = "qqConfigProperties")
    private QQConfigProperties properties;
    @Resource(name = "botManager")
    private BotManager botManager;

    @Bean(initMethod = "init")
    @ConditionalOnProperty(prefix = "bot",value = "enabled-reverse-ws",havingValue = "false",matchIfMissing = true)
    public WsSocketClient webSocketClient(ApplicationContext context) throws URISyntaxException {
        Map<String, CompressImage> ofType = context.getBeansOfType(CompressImage.class);
        Map<String, CacheImage> cacheImageMap = context.getBeansOfType(CacheImage.class);
        CompressImage compressImage = ofType.isEmpty() ? null : ofType.entrySet().iterator().next().getValue();
        CacheImage cacheImage = cacheImageMap.isEmpty() ? null : cacheImageMap.entrySet().iterator().next().getValue();
        QQEventHandler handler = new QQEventHandler(properties,compressImage,cacheImage,poolExecutor(),botManager);
        URI uri = new URI(properties.getWs());
        botManager.addHost(properties.getSelfId(),uri.getHost()+":"+uri.getPort());
        return new WsSocketClient(uri,  context, handler,properties.getCheckTime());
    }

    @Bean(initMethod = "init",destroyMethod = "destroy",name = "botThreadPoll")
    public BotThreadPoll poolExecutor(){
        return new BotThreadPoll(properties);
    }
}
