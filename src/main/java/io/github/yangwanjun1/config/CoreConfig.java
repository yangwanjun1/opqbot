package io.github.yangwanjun1.config;

import io.github.yangwanjun1.core.WsSocketClient;
import io.github.yangwanjun1.core.OpqProperties;
import io.github.yangwanjun1.core.OpqThreadPoll;
import io.github.yangwanjun1.event.WsTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;

@Configuration
@Slf4j
public class CoreConfig {

    @Resource(name = "opqProperties")
    private OpqProperties properties;
    @Bean(initMethod = "init")
    public WsSocketClient webSocketClient(ApplicationContext context) throws URISyntaxException {
        if (properties.getEnabledReverseWs()){
            return null;
        }
        WsSocketClient socket = new WsSocketClient(new URI(properties.getWs()),properties.getWelcome(),context);
        if (properties.getEnabledTask()) {
            new Timer().schedule(new WsTask(socket), 5000, 1000);
        }
        return socket;
    }
    @Bean(initMethod = "init",destroyMethod = "destroy")
    public OpqThreadPoll poolExecutor(){
        return new OpqThreadPoll(properties);
    }
}