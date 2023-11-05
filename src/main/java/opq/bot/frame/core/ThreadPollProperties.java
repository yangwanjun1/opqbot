package opq.bot.frame.core;

import lombok.Data;

@Data
public class ThreadPollProperties {
    private int core = 2;
    private int maxSize = 4;
    private int keepAliveTime = 30;
    private int blockSize = 50;
}
