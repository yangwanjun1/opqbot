package io.github.yangwanjun1.event.impl;

import io.github.yangwanjun1.constants.OptionType;
import io.github.yangwanjun1.constants.SendType;
import io.github.yangwanjun1.constants.SourceType;
import io.github.yangwanjun1.data.*;
import io.github.yangwanjun1.event.OpqMessageEvent;
import io.github.yangwanjun1.utils.OpqUtils;
import org.apache.hc.client5.http.fluent.Request;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 群消息事件
 */
public class GroupMessageEvent extends OpqMessageEvent {


    public GroupMessageEvent(EventData eventData, long currentQQ, Boolean photoCatch) {
        super(eventData,currentQQ,photoCatch);
    }

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
     * 发送多张图片到群【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendGroupImage(List<FileBody> imageList){
        return sendGroupImage(imageList,null);
    }

    /**
     * 发送图片到群【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendGroupImage(FileBody data){
        return sendGroupImage(null,data);
    }

    /**
     *
     * @param imageUrl 图片网络地址
     * @param f 压缩后的图片质量
     */
    public ResultData sendGroupImage(String imageUrl,double f) throws IOException {
        byte[] bytes = Request.get(imageUrl).execute().returnContent().asBytes();
        String base = OpqUtils.compress(bytes,f);
        FileBody body = OpqUtils.fileBody(null, base, null, OptionType.GROUP_IMAGE, getSelfId(), null);
        return sendGroupImage(null,body);
    }
    public ResultData sendGroupImage(String imageUrl) throws IOException {
        return sendGroupImage(imageUrl,0.8);
    }
    /**
     * 发送图片到群【记得压缩图片，防止图片出现感叹号】
     * @param f 图片压缩质量 >= 1时不压缩
     * @param data 本地文件
     */
    public ResultData sendGroupImage(File data, double f){
        return sendGroupImage(null,data,f);
    }
    public ResultData sendGroupImage(File data){
        return sendGroupImage(null,data,0.8);
    }
    /**
     * 发送图片到群【记得压缩图片，防止图片出现感叹号】
     * @param f 图片压缩质量 >= 1时不压缩
     * @param text 文本
     * @param image 本地文件
     */
    public ResultData sendGroupImage(String text,File image, double f){
        FileBody images = getImageCatch(image,f);
        return sendGroupImage(text,images);
    }
    /**
     * 发送图文到群
     */
    public ResultData sendGroupImage(String content,FileBody data){
        String body = msgBody(content,getGroup().getGroupCode(), List.of(data),null);
        return sendMsg(getSelfId(),body, ResultData.class);
    }
    /**
     * 发送多张图片到群并at用户【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendGroupImage(List<FileBody> imageList,List<AtUinLists> atUinLists){
        return sendGroupImage(null,imageList,atUinLists);
    }
    /**
     * 发送多张图文到群并at用户【记得压缩图片，防止图片出现感叹号】
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
        SendMsgBody data = OpqUtils.leaveTheGroupBody(getGroup().getGroupCode());
        return sendMsg(getSelfId(),OpqUtils.toJsonString(data),ResultEventData.class);
    }

    /**
     * 踢用户
     */
    public ResultData removeUser(String uid){
        SendMsgBody body = new SendMsgBody(SendType.SSO_GROUP_OP,new CgiRequest());
        body.getCgiRequest().setOpCode(2208);
        body.getCgiRequest().setUin(getGroup().getGroupCode());
        body.getCgiRequest().setUid(uid);
        String string = OpqUtils.toJsonString(body);
        return sendMsg(getSelfId(),string, ResultData.class);
    }

    /**
     * 获取群成员列表
     */
    public List<GroupData> groupList(){
        CgiRequest request = new CgiRequest();
        request.setUin(getGroup().getGroupCode());
        request.setLastBuffer("");
        SendMsgBody body = new SendMsgBody(SendType.GROUP_LIST,request);
        GroupList groupList = sendMsg(getSelfId(), OpqUtils.toJsonString(body), GroupList.class);
        return groupList.getResponseData().getMemberLists();
    }
}
