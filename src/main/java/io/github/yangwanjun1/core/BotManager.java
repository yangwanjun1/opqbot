package io.github.yangwanjun1.core;

import io.github.yangwanjun1.event.Bot;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class BotManager {

    private final Map<Long, Bot> botManager = new ConcurrentHashMap<>();

    public void add(Long qq, Bot bot) {
        botManager.put(qq,bot);
    }
    public String getHostAndPort(Long qq){
        if (botManager.containsKey(qq)) {
            return botManager.get(qq).getIpPort();
        }
        return null;
    }
    public long removeSession(WebSocketSession session){
        for (Map.Entry<Long, Bot> entry : botManager.entrySet()) {
            if (session == entry.getValue().getSession()){
                botManager.remove(entry.getKey());
                return entry.getKey();
            }
        }
        return 0;
    }
}
