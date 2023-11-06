package opq.bot.frame.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import opq.bot.frame.annotation.Opq;
import opq.bot.frame.annotation.OpqListener;
import opq.bot.frame.core.OpqThreadPoll;
import opq.bot.frame.core.OpqProperties;
import opq.bot.frame.core.OpqWebSocket;
import opq.bot.frame.event.impl.GroupMessageEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.TimerTask;

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