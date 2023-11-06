package opq.bot.frame.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import opq.bot.frame.event.OpqListenerEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.context.ApplicationContext;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class OpqWebSocket extends WebSocketClient {
    private ApplicationContext context;
    @Getter
    private static String host;
    @Getter
    private final  Timer timer = new Timer();
    private WsTaskTimer task;
    public void setContext(ApplicationContext context) {
        this.context = context;
        host = getURI().getHost()+":"+getURI().getPort();
        this.task = new WsTaskTimer(this);
    }

    public OpqWebSocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("opq连接成功");
    }

    @Override
    public void onMessage(String message) {
        if (EventHandlerAdapter.eventIsEmpty()){
            return;
        }
        context.publishEvent(new OpqListenerEvent(message));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("连接断开，正在尝试连接中...");
        timer.schedule(task, 1000L);
    }

    @Override
    public void onError(Exception ex) {
        log.error("连接错误，请检查网络是否正常");
    }
}
@Slf4j
class WsTaskTimer extends TimerTask {

    private OpqWebSocket client;
    private int count = 0;
    public WsTaskTimer(OpqWebSocket client){
        this.client = client;
    }
    @Override
    public void run() {
        if (!client.isOpen() || count<=50){
            log.info("正在第{}次重连",++count);
            client.reconnect();
            if (client.isOpen()){
                client.getTimer().cancel();
            }
        }
        if (count>50){
            client.getTimer().cancel();
            log.info("连接达到限制，请检查服务是否正常");
        }
    }
}
