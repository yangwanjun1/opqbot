package opq.bot.frame.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import opq.bot.frame.core.OpqThreadPoll;
import opq.bot.frame.core.OpqProperties;
import opq.bot.frame.core.OpqWebSocket;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;

@Configuration
@Slf4j
public class CoreConfig {

    @Resource(name = "opqProperties")
    private OpqProperties properties;
    @Bean
    public OpqWebSocket webSocketClient(ApplicationContext context) throws URISyntaxException {
        OpqWebSocket socket = new OpqWebSocket();
        socket.init(properties.getWs());
        socket.getClient().connect();
        socket.setContext(context);
        return socket;
    }
    @Bean(initMethod = "init",destroyMethod = "destroy")
    public OpqThreadPoll poolExecutor(){
        return new OpqThreadPoll(properties);
    }
}
