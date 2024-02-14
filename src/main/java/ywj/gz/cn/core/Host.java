package ywj.gz.cn.core;

import lombok.NonNull;
import org.springframework.web.socket.WebSocketSession;
import ywj.gz.cn.config.QQConfigProperties;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Host {
    private static final Map<Long,String> hostMap = new ConcurrentHashMap<>();

    public static String getHost(long qq){
        return hostMap.getOrDefault(qq,"127.0.0.1:8086");
    }
    public static void noContainsKey(long qq, @NonNull WebSocketSession session, QQConfigProperties properties){
        URI uri = session.getUri();
        assert uri != null;
        String host = uri.getHost();
        int port = !properties.getEnabledReverseWs() ? uri.getPort() : properties.getReversePort();
        String andPort = host + ":" + port;
        Host.addHost(qq, andPort);
    }
    public static void noContainsKey(long qq, QQConfigProperties properties){
        Host.addHost(qq,properties.getSelfId()+":"+properties.getReversePort());
    }
    public static void remove(long qq){
        hostMap.remove(qq);
    }

    public static void addHost(long qq,String hostAndPort){
        hostMap.put(qq,hostAndPort);
    }

}
