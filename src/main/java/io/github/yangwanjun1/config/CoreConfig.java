package io.github.yangwanjun1.config;

import io.github.yangwanjun1.core.OpqProperties;
import io.github.yangwanjun1.core.OpqThreadPoll;
import io.github.yangwanjun1.core.OpqWebSocket;
import io.github.yangwanjun1.event.WsTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.drafts.Draft_6455;
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

    @Bean
    public OpqWebSocket webSocketClient(ApplicationContext context) throws URISyntaxException {
        OpqWebSocket socket = new OpqWebSocket(new URI(properties.getWs()),new Draft_6455());
        socket.setContext(context);
        socket.connect();
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