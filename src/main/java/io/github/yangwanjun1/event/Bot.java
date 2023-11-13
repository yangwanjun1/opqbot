package io.github.yangwanjun1.event;

import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.data.AtUinLists;
import io.github.yangwanjun1.data.FileBody;
import io.github.yangwanjun1.utils.OpqUtils;
import lombok.Getter;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Bot{

    @Getter
    private final long qq;

    private final String host;

    public Bot(Long qq, String host) {
        this.qq = qq;
        String format = "http://%s/v1/LuaApiCaller?funcname=MagicCgiCmd&timeout=10&qq=%s";
        this.host = String.format(format, host,qq);
    }
    /**
     * 由于当前框架暂时没用获取到qq信息，仅在qq连接成功之后并且有消息时才会记录bot信息，否则不可用
     * ================================================================================
     */
    public void sendFriendMsg(String msg, long userId){
       sendFriendImage(msg,userId,null);
    }
    public void sendFriendImage(FileBody image, long userId){
        sendFriendImage(null,image,userId);
    }
    public void sendFriendImage(String msg, FileBody images,long userId){
        sendFriendImage(msg,userId,images == null ? null : List.of(images));
    }
    public void sendFriendImage(String msg, long userId, List<FileBody> images){
        String body = OpqUtils.msgBody(SourceType.FRIEND.getType(),msg,userId,images,null);
        sendMsg(body);
    }

    /**
     * ================================================================================
     */
    public void sendGroupMsg(String msg,long groupId){
        sendGroupAtMsg(msg,groupId,null);
    }
    public void sendGroupMsg(String msg,FileBody image,long groupId){
        sendGroupAtImageMsg(msg,groupId,List.of(image),null);
    }
    public void sendGroupMsg(String msg,List<FileBody> image,long groupId){
        sendGroupAtImageMsg(msg,groupId,image,null);
    }
    public void sendGroupAtMsg(String msg,long groupId,AtUinLists atUinLists){
        sendGroupAtImageMsg(msg,groupId,null,atUinLists ==null?null:List.of(atUinLists));
    }

    public void sendGroupAtImageMsg(String msg,long groupId,List<FileBody> images,List<AtUinLists> atUinLists){
        String body = OpqUtils.msgBody(SourceType.GROUP.getType(),msg,groupId,images,atUinLists);
        sendMsg(body);
    }
    /**
     * ================================================================================
     */
    private void sendMsg(String body){
        Request posted = Request.post(host);
        try {
            posted.bodyString(body, ContentType.APPLICATION_JSON);
            posted.execute().returnContent().asString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
