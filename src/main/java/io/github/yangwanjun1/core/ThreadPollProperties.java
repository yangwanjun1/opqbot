package io.github.yangwanjun1.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "opq.thread-poll")
public class ThreadPollProperties {
    private int core = 2;
    private int maxSize = 4;
    private int keepAliveTime = 30;
    private int blockSize = 50;
}
