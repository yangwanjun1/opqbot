package io.github.yangwanjun1.event;

import io.github.yangwanjun1.core.WsSocketClient;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.WebSocketContainer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.TimerTask;

@Slf4j
public class WsTask extends TimerTask {
    private  int count = 1;
    private final WsSocketClient socket;

    public WsTask(WsSocketClient socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket.getSession() == null){
            return;
        }
        if (socket.getSession().isOpen() && count !=1){
            count = 1;
            log.info("opq连接成功");
            return;
        }
        if (count == 1 && !socket.getSession().isOpen()){
            log.error("连接断开，正在尝试连接...");
        }
        if (count > 100){
            cancel();
            log.info("连接达到限制，请检查网络是否正常");
        }
        if (!socket.getSession().isOpen() && count<=100) {
            log.error("opq连接断开，正在尝试第{}次连接",count++);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            try {
                container.connectToServer(socket,socket.getUri());
            } catch (DeploymentException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
