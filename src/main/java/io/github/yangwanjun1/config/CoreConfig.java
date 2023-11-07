package io.github.yangwanjun1.config;

import io.github.yangwanjun1.core.OpqProperties;
import io.github.yangwanjun1.core.OpqWebSocket;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import io.github.yangwanjun1.core.OpqThreadPoll;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Slf4j
public class CoreConfig {

    @Resource(name = "opqProperties")
    private OpqProperties properties;

    @Bean
    public OpqWebSocket webSocketClient(ApplicationContext context) throws URISyntaxException {
        OpqWebSocket socket = new OpqWebSocket(new URI(properties.getWs()));
        socket.setContext(context);
        socket.connect();
        return socket;
    }
    @Bean(initMethod = "init",destroyMethod = "destroy")
    public OpqThreadPoll poolExecutor(){
        return new OpqThreadPoll(properties);
    }
}