package io.github.yangwanjun1.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "opq")
public class OpqProperties {
    /**
     * ws连接地址
     */
    private String ws = "ws://127.0.0.1:9000/ws";
    /**
     * 线程池
     */
    private ThreadPollProperties threadPoll;
    /**
     * 是否开启自动重连(默认false)，会开启一个定时任务
     */
    private Boolean enabledTask = true;
    /**
     * 控制台欢迎语（默认：欢迎使用opqbot）
     */
    private String welcome = "欢迎使用OPQBOT";
    public ThreadPollProperties getThreadPoll() {
        if (threadPoll == null){
            threadPoll = new ThreadPollProperties();
        }
        return threadPoll;
    }
}
