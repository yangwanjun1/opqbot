package ywj.gz.cn.core.ws;

import jakarta.websocket.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import ywj.gz.cn.core.handler.QQEventHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@ClientEndpoint
public class WsSocketClient {
    private final ApplicationContext context;
    private final QQEventHandler qqEventHandler;

    @Getter
    private final URI uri;
    @Getter
    private Session qqSession;
    private volatile Timer timer ;
    private final Integer checkTime;

    public WsSocketClient(URI serverUri, ApplicationContext context, QQEventHandler qqEventHandler, Integer checkTime){
        this.uri = serverUri;
        this.context = context;
        this.qqEventHandler = qqEventHandler;
        this.checkTime = checkTime;
    }
    public void init() throws DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this,uri);
    }

    @OnOpen
    public void onOpen(Session session){
        this.qqSession = session;
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        log.info("{}","连接成功");
    }

    @OnMessage
    public void onMessage(String message,Session session){
        qqEventHandler.handler(message,context);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.error("连接关闭，状态码:{}->原因:{}",closeReason.getCloseCode(),closeReason.getReasonPhrase());
        restConnect();
    }

    @OnError
    public void onError(Throwable e){
        log.error("connect error:{},try connected server...",e.getMessage());
        restConnect();
    }

    public synchronized void restConnect(){
        if (timer == null){
            timer = new Timer("wsRestConnected");
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        init();
                    } catch (DeploymentException | IOException e) {
                        log.error("error:{}",e.getMessage());
                    }
                }
            },1000,checkTime * 1000L);
        }
    }
}