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
     * 反向ws地址
     */
    private String reverseWs = "/ws";
    /**
     * opq端口，默认是9000
     */
    private Integer reversePort = 9000;
    /**
     * 线程池
     */
    private ThreadPollProperties threadPoll;
    /**
     * 是否开启自动重连(默认true)，会开启一个定时任务
     */
    private Boolean enabledTask = true;
    /**
     * 反向ws，开启reverseWs时ws无效
     */
    private Boolean enabledReverseWs = false;
    /**
     * 是否过滤自己的消息，默认true
     */
    private Boolean filterBot = true;
    /**
     * 是否开启图片缓存功能，默认false
     */
    private Boolean photoCatch = false;
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
