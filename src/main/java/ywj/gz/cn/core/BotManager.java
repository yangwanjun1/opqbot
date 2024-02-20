package ywj.gz.cn.core;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.WebSocketSession;
import ywj.gz.cn.body.receive.ClusterInfoBody;
import ywj.gz.cn.config.QQConfigProperties;
import ywj.gz.cn.util.MsgUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class BotManager {

    private static final Map<Long,String> botMap = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext context;


    /**
     * 在线的bot实例
     */
    @Getter
    private final Map<Long,Bot> botList = new ConcurrentHashMap<>();


    @PostConstruct
    public void init(){
        Map<String, BotPluginList> beans = context.getBeansOfType(BotPluginList.class);
        if (!beans.isEmpty()){
            Map<String, CacheImage> cacheImageMap = context.getBeansOfType(CacheImage.class);
            Map<String, CompressImage> compressImageMap = context.getBeansOfType(CompressImage.class);
            Iterator<BotPluginList> iterator = beans.values().iterator();
            iterator.next().getBotList().forEach(q->botList.put(q.selfId(),new Bot(q.host(),q.selfId(),
                    compressImageMap.isEmpty()?null:compressImageMap.values().iterator().next(),
                    cacheImageMap.isEmpty()?null:cacheImageMap.values().iterator().next())));
            log.info("bot列表初始化成功，配置读取实例总共有{}个",botList.size());
        }
    }

    /**
     * 初始化bot列表(opq多实例下可用)
     * @param host ip地址
     * @param port 端口
     * @param qq 机器人id
     */
    public void initPostBotList(String host, int port, Long qq) throws IOException, InterruptedException {
        if ((qq == null || qq == 0L) && !botMap.isEmpty()){
            qq = botMap.keySet().iterator().next();
        }
        String url = "http://" + host + ":" + port + "/v1/LuaApiCaller?funcname=MagicCgiCmd&timeout=10&isShow=true&qq=" + qq;
        HttpRequest request = MsgUtils.httpRequest(url, RequestMethod.POST, """
                {
                  "CgiCmd": "ClusterInfo",
                  "CgiRequest": {}
                }
                """);
        cluster(host, port, request);
    }

    /**
     * 获取opq实例
     */
    @Deprecated
    public ClusterInfoBody initGetBotList(String host, int port) throws IOException, InterruptedException {
        HttpRequest request = MsgUtils.httpRequest( "http://" + host+":"+port + "/v1/clusterinfo?isShow=true", RequestMethod.GET,null);
        return cluster(host, port, request);
    }
    /**
     * 获取opq实例
     */
    @Deprecated
    public ClusterInfoBody initGetBotList() throws IOException, InterruptedException, URISyntaxException {
        Iterator<Map.Entry<Long, String>> iterator = botMap.entrySet().iterator();
        if (!iterator.hasNext()){
            return null ;
        }
        String host = iterator.next().getValue();
        HttpRequest request = MsgUtils.httpRequest( "http://" + host+ "/v1/clusterinfo?isShow=true", RequestMethod.GET,null);
        URI uri = new URI(host);
        return cluster(host, uri.getPort(), request);
    }

    private ClusterInfoBody cluster(String host, int port, HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        ClusterInfoBody bean = MsgUtils.toBean(response.body(), ClusterInfoBody.class);
        if (bean !=null && bean.getCgiBaseResponse().getRet() != -1){
            log.info("bot列表初始化成功，在线实例总共有{}个",bean.getClusterInfo().getQqUsers().size());
            Map<String, CacheImage> cacheImageMap = context.getBeansOfType(CacheImage.class);
            Map<String, CompressImage> compressImageMap = context.getBeansOfType(CompressImage.class);
            bean.getClusterInfo().getQqUsers().forEach(q->botList.put(q.getUin(),new Bot(host+":"+port,q.getUin(),
                    compressImageMap.isEmpty()?null:compressImageMap.values().iterator().next(),
                    cacheImageMap.isEmpty()?null:cacheImageMap.values().iterator().next())));
        }
        return bean;
    }


    public Map<Long,String> getBotManager(){
        return botMap;
    }

    public static String getHost(Long qq){
        return botMap.get(qq);
    }

    public void noContainsKey(Long qq, @NonNull WebSocketSession session, QQConfigProperties properties){
        if (botMap.containsKey(qq)){
            return;
        }
        URI uri = session.getUri();
        assert uri != null;
        String host = uri.getHost();
        int port = !properties.getEnabledReverseWs() ? uri.getPort() : properties.getReversePort();
        String andPort = host + ":" + port;
        addHost(qq, andPort);
    }

    public void addHost(Long qq,String hostAndPort){
        botMap.put(qq,hostAndPort);
        Map<String, CacheImage> cacheImageMap = context.getBeansOfType(CacheImage.class);
        Map<String, CompressImage> compressImageMap = context.getBeansOfType(CompressImage.class);
        botList.put(qq, new Bot(hostAndPort, qq,
                compressImageMap.isEmpty()?null:compressImageMap.values().iterator().next(),
                cacheImageMap.isEmpty()?null:cacheImageMap.values().iterator().next()));
    }

}
