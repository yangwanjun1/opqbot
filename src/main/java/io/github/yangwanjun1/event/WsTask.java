package io.github.yangwanjun1.event;

import io.github.yangwanjun1.core.OpqWebSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

@Slf4j
public class WsTask extends TimerTask {
    private  int count = 1;
    private final OpqWebSocket socket;

    public WsTask(OpqWebSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket.isOpen()){
            count = 1;
            return;
        }
        if (count > 100){
            cancel();
            log.info("连接达到限制，请检查网络是否正常");
        }
        if (!socket.isOpen() && count<=100) {
            log.error("opq连接断开，正在尝试第{}次连接",count++);
            socket.reconnect();
        }
    }
}
