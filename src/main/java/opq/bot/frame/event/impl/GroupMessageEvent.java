package opq.bot.frame.event.impl;

import opq.bot.frame.constants.SourceType;
import opq.bot.frame.data.*;
import opq.bot.frame.event.OpqMessageEvent;
import opq.bot.frame.utils.OpqUtils;
import org.springframework.http.HttpEntity;

import java.io.File;
import java.util.List;
import java.util.Map;

public class GroupMessageEvent extends OpqMessageEvent {

    /**
     * 回复群用户
     */
    public ResultData replay(String content){
        return sendMsgAtUser(content,getUserInfo().getUserId());
    }

    /**
     * 发送群消息
     */
    public ResultData sendGroupMsg(String content){
        String body = OpqUtils.msgBody(SourceType.GROUP.getType(),content,getGroup().getGroupCode(),null,null);
        return sendMsg(getSelfId(),body, ResultData.class);
    }

    /**
     * 发送群at消息
     */
    public ResultData sendMsgAtUser(String content,long userId){
        return sendMsgAtUser(content,Map.of(userId, getGroup().getGroupCard()));
    }

    /**
     * 回复群多个用户
     * @param atUserMap key:用户id value：用户昵称
     */
    public ResultData sendMsgAtUser(String content, Map<Long,String> atUserMap){
        String body = OpqUtils.msgBody(SourceType.GROUP.getType(),content,getGroup().getGroupCode(),null, OpqUtils.atUinLists(atUserMap));
        return sendMsg(getSelfId(),body, ResultData.class);
    }

    /**
     * 发送多张图片到群
     */
    public ResultData sendGroupImage(List<FileBody> imageList){
        return sendGroupImage(imageList,null);
    }

    /**
     * 发送网络图片到群
     */
    public ResultData sendGroupImage(String url){
        return sendGroupImage(null,url);
    }

    /**
     * 发送图文到群
     */
    public ResultData sendGroupImage(String content,String url){
        HttpEntity<String> entity = OpqUtils.uploadImageFileBody(url, SourceType.GROUP.getType(), true);
        FileBody data = uploadImageFile(getSelfId(), entity);
        String body = msgBody(content,getGroup().getGroupCode(),List.of(data),null);
        return sendMsg(getSelfId(), body, ResultData.class);
    }
    /**
     * 发送本地图片到群
     */
    public ResultData sendGroupImage(File file){
        return sendGroupImage(null,file);
    }
    /**
     * 发送图文到群
     */
    public ResultData sendGroupImage(String content,File file){
        HttpEntity<String> entity = OpqUtils.uploadImageFileBody(file, SourceType.GROUP.getType());
        FileBody data = uploadImageFile(getSelfId(), entity);
        String body = msgBody(content,getGroup().getGroupCode(), List.of(data),null);
        return sendMsg(getSelfId(),body, ResultData.class);
    }
    /**
     * 发送多张图片到群并at用户
     */
    public ResultData sendGroupImage(List<FileBody> imageList,List<AtUinLists> atUinLists){
        return sendGroupImage(null,imageList,atUinLists);
    }
    /**
     * 发送多张图文到群并at用户
     */
    public ResultData sendGroupImage(String content,List<FileBody> imageList,List<AtUinLists> atUinLists){
        String body = msgBody(content,getGroup().getGroupCode(),imageList,atUinLists);
        return sendMsg(getSelfId(),body, ResultData.class);
    }
    public String msgBody(String content,long groupId,List<FileBody> imageList,List<AtUinLists> atUinLists){
       return OpqUtils.msgBody(SourceType.GROUP.getType(),content,groupId,imageList, atUinLists);
    }
    /**
     * 退群
     */
    public ResultEventData leaveTheGroup(){
        FileData data = OpqUtils.leaveTheGroupBody(getGroup().getGroupCode());
        return sendMsg(getSelfId(),OpqUtils.toJsonString(data),ResultEventData.class);
    }
}
