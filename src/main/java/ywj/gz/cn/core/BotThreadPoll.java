package ywj.gz.cn.core;


import ywj.gz.cn.config.QQConfigProperties;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BotThreadPoll {
    private ThreadPoolExecutor threadPoolExecutor;
    private final QQConfigProperties qqConfigProperties;
    public BotThreadPoll(QQConfigProperties opqProperties){
        this.qqConfigProperties = opqProperties;
    }
    public void execute(Runnable runnable){
        threadPoolExecutor.execute(runnable);
    }

    public void init(){
        int maxSize = qqConfigProperties.getThreadPollProperties().getMaxSize();
        int core = qqConfigProperties.getThreadPollProperties().getCore();
        int blockSize = qqConfigProperties.getThreadPollProperties().getBlockSize();
        int keepAliveTime = qqConfigProperties.getThreadPollProperties().getKeepAliveTime();
        threadPoolExecutor = new ThreadPoolExecutor(core, maxSize, keepAliveTime,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(blockSize), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void destroy(){
        threadPoolExecutor.shutdownNow();
    }
}
