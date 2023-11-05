package opq.bot.frame.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "opq")
public class OpqProperties {
    private String ws = "ws://127.0.0.1:9000/ws";
    private ThreadPollProperties threadPoll;

    public ThreadPollProperties getThreadPoll() {
        if (threadPoll == null){
            threadPoll = new ThreadPollProperties();
        }
        return threadPoll;
    }
}
