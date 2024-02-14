package ywj.gz.cn.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("qqConfigProperties")
@Data
@ConfigurationProperties(prefix = "bot")
public class QQConfigProperties {
    /**
     * ws连接地址（opq的ws地址）
     */
    private String ws = "ws://127.0.0.1:8086/ws";
    /**
     * 正向连接时的qq，通过配置该项可实现主动发送消息
     */
    private Long selfId = 0L;
    /**
     * 反向ws地址(默认：ws://127.0.0.1:程序端口/ws)
     */
    private String reverseWs = "/ws";
    /**
     * opq端口，默认是8086（opq客户端端口）
     */
    private Integer reversePort = 8086;
    /**
     * 事件线程池
     */
    private ThreadPollProperties threadPollProperties;
    /**
     * 间隔时间检测ws连接是否正常，单位秒
     */
    private Integer checkTime = 5;
    /**
     * 反向ws，开启reverseWs时ws无效
     */
    private Boolean enabledReverseWs = false;
    /**
     * 是否过滤bot的消息，默认true
     */
    private Boolean filterBot = true;

    public ThreadPollProperties getThreadPollProperties() {
        if (threadPollProperties == null){
            threadPollProperties = new ThreadPollProperties();
        }
        return threadPollProperties;
    }
}
