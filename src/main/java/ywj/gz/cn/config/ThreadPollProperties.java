package ywj.gz.cn.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bot.thread-poll-properties")
public class ThreadPollProperties {
    /**
     * 核心数
     */
    private int core = 2;
    /***
     * 最大线程数
     */
    private int maxSize = 8;
    /**
     * 存活时间
     */
    private int keepAliveTime = 30;
    /**
     * 队列容量
     */
    private int blockSize = 50;
}
