package io.github.yangwanjun1.core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OpqThreadPoll {
    private static ThreadPoolExecutor threadPoolExecutor;
    private final OpqProperties opqProperties;
    public OpqThreadPoll(OpqProperties opqProperties){
        this.opqProperties = opqProperties;
    }
    public static ThreadPoolExecutor getThreadPoll(){
        return  threadPoolExecutor;
    }

    public void init(){
        int maxSize = opqProperties.getThreadPoll().getMaxSize();
        int core = opqProperties.getThreadPoll().getCore();
        int blockSize = opqProperties.getThreadPoll().getBlockSize();
        int keepAliveTime = opqProperties.getThreadPoll().getKeepAliveTime();
        threadPoolExecutor = new ThreadPoolExecutor(core, maxSize, keepAliveTime,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(blockSize), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void destroy(){
        threadPoolExecutor.shutdownNow();
    }
}
