package io.github.yangwanjun1.event;

import io.github.yangwanjun1.constants.OptionCode;
import io.github.yangwanjun1.constants.SendType;
import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.data.*;
import io.github.yangwanjun1.utils.OpqUtils;
import lombok.Getter;
import lombok.NonNull;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class Bot{

    @Getter
    private final long qq;

    private final String host;
    @Getter
    private final String ipPort;
    @Getter
    private final WebSocketSession session;

    public Bot(Long qq, String host, @NonNull WebSocketSession session) {
        this.qq = qq;
        this.host = "http://"+host+"/v1/LuaApiCaller?funcname=MagicCgiCmd&timeout=10&qq="+qq;
        this.ipPort = host;
        this.session = session;
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
        sendMsg(body,ResultData.class);
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
        sendMsg(body,ResultData.class);
    }
    /**
     * ================================================================================
     */
    private <T>T sendMsg(String body,Class<T> cls){
        Request posted = Request.post(host);
        try {
            posted.bodyString(body, ContentType.APPLICATION_JSON);
            String string = posted.execute().returnContent().asString(StandardCharsets.UTF_8);
            return OpqUtils.toBean(string,cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Friend> friendList(Long startUin){
        try {
            SendMsgBody body = new SendMsgBody(SendType.FRIEND_LIST,new CgiRequest());
            body.getCgiRequest().setLastUin(startUin);
            ResultData data = sendMsg(OpqUtils.toJsonString(body), ResultData.class);
            return data.getResponseData().getFriendLists();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    public  List<Friend> friendList(){
        return friendList(0L);
    }
    /**
     * 退群
     */
    public ResultEventData leaveTheGroup(Long group){
        SendMsgBody data = OpqUtils.leaveTheGroupBody(group);
        return sendMsg(OpqUtils.toJsonString(data),ResultEventData.class);
    }

    /**
     * 踢用户
     */
    public ResultData removeUser(String uid,long group){
        SendMsgBody body = new SendMsgBody(SendType.SSO_GROUP_OP,new CgiRequest());
        body.getCgiRequest().setOpCode(OptionCode.REMOVE_GROUPER.getCode());
        body.getCgiRequest().setUin(group);
        body.getCgiRequest().setUid(uid);
        String string = OpqUtils.toJsonString(body);
        return sendMsg(string, ResultData.class);
    }

    /**
     * 获取群列表
     */
    public List<Group> groupList(long group){
        SendMsgBody body = new SendMsgBody(SendType.GROUP_LIST,new CgiRequest());
        ResultData data = sendMsg(OpqUtils.toJsonString(body), ResultData.class);
        return data.getResponseData().getGroupLists();
    }
}
