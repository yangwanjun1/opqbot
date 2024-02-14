package ywj.gz.cn.event.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ywj.gz.cn.body.pojo.*;
import ywj.gz.cn.body.receive.CgiBaseResponse;
import ywj.gz.cn.body.receive.CommonlyResponseBody;
import ywj.gz.cn.body.receive.ResponseData;
import ywj.gz.cn.body.send.CgiRequest;
import ywj.gz.cn.body.send.SendMsgBody;
import ywj.gz.cn.constants.OptionType;
import ywj.gz.cn.constants.SendType;
import ywj.gz.cn.constants.SourceType;
import ywj.gz.cn.core.CacheImage;
import ywj.gz.cn.core.CompressImage;
import ywj.gz.cn.event.QQBotEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
@Getter
public class GroupEvent extends QQBotEvent {
    private final GroupInfo group;//群信息
    @Setter
    private String content;
    private final Files video;//视频
    private final Files voice;//语音
    private final List<Files> images;//图片
    private final Long msgSeq;
    private final Long msgTime;
    @Setter
    private List<AtUinLists> atUinLists;//被at的人都会在集合中
    private final Files file;//群文件上传或者好友发送文件时会有内容
    private final String senderUid;//消息来源uid
    private final Long senderUin;
    private final long msgRandom;
    private final TemporarilyEvent temporarilyEvent;//仅发送临时消息，收不到数据信息
    public GroupEvent(MessageData message, CompressImage compress, CacheImage cacheImage) {
        super(message.getCurrentQQ(),message.getCurrentPacket().getEventData().getMsgHead().getMsgUid(),compress,cacheImage);
        EventData data = message.getCurrentPacket().getEventData();
        this.video = data.getMsgBody().getVideo();
        this.file = data.getMsgBody().getFile();
        this.images = data.getMsgBody().getImages();
        this.voice = data.getMsgBody().getVoice();
        this.content = data.getMsgBody().getContent();
        this.atUinLists = data.getMsgBody().getAtUinLists();
        this.senderUin = data.getMsgHead().getSenderUin();
        this.senderUid = data.getMsgHead().getSenderUid();
        this.group = data.getMsgHead().getGroupInfo();
        this.msgSeq = data.getMsgHead().getMsgSeq();
        this.msgTime = data.getMsgHead().getMsgTime();
        this.msgRandom = data.getMsgHead().getMsgRandom();
        this.temporarilyEvent = new TemporarilyEvent(message,compress,cacheImage);
    }
    public User getUserInfo() {
        return queryUserInfo(senderUid);
    }

    public void sendContent(@NonNull String text){
        sendMessage(text,null,null,null);
    }
    public void sendAtUserText(@NonNull String text, long userId , @Nullable String nick){
        sendMessage(text,null,List.of(new AtUinLists(nick,null,userId)),null);
    }

    /**
     * 引用回复
     */
    public void reply(String text){
        CgiRequest request = new CgiRequest();
        ReplyTo replyTo = new ReplyTo();
        replyTo.setMsgSeq(msgSeq);
        replyTo.setMsgUid(getMsgId());
        replyTo.setMsgTime(msgTime);
        request.setReplyTo(replyTo);
        request.setContent(text);
        request.setAtUinLists(List.of(new AtUinLists(group.getGroupCard(),null,senderUin)));
        request.setToType(SourceType.GROUP.getType());
        request.setToUin(getGroup().getGroupCode());
        SendMsgBody body = new SendMsgBody(SendType.SEND_MSG, request);
        send(body);
    }

    /**
     * 发送语音
     */
    public void sendVoice(@NonNull Files voice){
       sendMessage(null,null,null,voice);
    }
    /**
     * 发送图片
     */
    public void sendImage(@NonNull List<Files> images){
       sendMessage(null,images,null,null);
    }

    /**
     * 可自定义构造消息组合
     */
    public void sendMessage(String text, List<Files> images, List<AtUinLists> at,Files voiceBody){
        sendMsg(text,images,at,voiceBody);
    }
    /**
     * 踢本群用户（需要是管理员）
     */
    public CgiBaseResponse removeUser(@NonNull String uid){
        CgiRequest request = new CgiRequest();
        request.setOpCode(OptionType.REMOVE_GROUPER.getType());
        request.setUin(getGroup().getGroupCode());
        request.setUid(uid);
        SendMsgBody body = new SendMsgBody(SendType.SSO_GROUP_OP, request);
        CommonlyResponseBody send = send(body, CommonlyResponseBody.class);
        return send.getCgiBaseResponse();
    }
    /**
     * 禁言（需要是管理员）
     * 单位秒 至少60秒 至多30天 禁言一天为24*3600=86400 参数为0解除禁言
     */
    public CgiBaseResponse banUser(@NonNull String uid,int time){
        if (time<60){
            throw new RuntimeException("至少禁言一分钟");
        }
        CgiRequest request = new CgiRequest();
        request.setOpCode(OptionType.BEN_GROUPER.getType());
        request.setUin(getGroup().getGroupCode());
        request.setUid(uid);
        request.setBanTime(time);
        SendMsgBody body = new SendMsgBody(SendType.SSO_GROUP_OP, request);
        CommonlyResponseBody send = send(body, CommonlyResponseBody.class);
        return send.getCgiBaseResponse();
    }

    /**
     * 获取群成员列表
     */
    public ResponseData groupMemberList(@NonNull String lastBuffer){
        CgiRequest request = new CgiRequest();
        request.setUin(getGroup().getGroupCode());
        request.setLastBuffer(lastBuffer);
        SendMsgBody body = new SendMsgBody(SendType.GROUP_MEMBER_LIST,request);
        CommonlyResponseBody send = send(body, CommonlyResponseBody.class);
        return send.getResponseData();
    }
    public ResponseData groupMemberList(){
        return groupMemberList("");
    }
    private void sendMsg(String content, List<Files> images, List<AtUinLists> at,Files voiceBody){
        SendMsgBody body = super.msgBody(SourceType.GROUP.getType(), content, getGroup().getGroupCode(), images, at, voiceBody);
        send(body);
    }

    /**
     * 上传群组文件(使用linux文件分隔符 / )
     * @param filePath 文件的绝对路径
     * @param fileName 文件名
     */
    public CgiBaseResponse uploadFile(String filePath, long groupId, String fileName) throws IOException, InterruptedException {
        return uploadFile(filePath,groupId,fileName,CommonlyResponseBody.class).getCgiBaseResponse();
    }
    @Override
    public OptionType getOptionType() {
        return OptionType.GROUP_IMAGE;
    }

    @Override
    protected void sendImage(@NonNull File image) {
        Files files = null;
        try {
            files = uploadImage(image.getAbsolutePath());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToImage(files);
    }

    @Override
    protected void sendUrlImage(@NonNull String url) {
        Files files = null;
        try {
            files = uploadUrlImage(url);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToImage(files);
    }

    @Override
    protected void sendBaseImage(@NonNull String base64) {
        Files files = null;
        try {
            files = uploadBase64Image(base64);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToImage(files);
    }
    private void sendToImage(Files image){
        SendMsgBody body = msgBody(SourceType.GROUP.getType(), null, group.getGroupCode(), List.of(image), null, null);
        send(body);
    }

    /**
     * 撤回本条消息
     */
    public void withdrawMsg(){
        withdrawMsg(msgSeq,msgRandom);
    }

    /**
     * 撤回消息
     */
    public void withdrawMsg(long msgSeq,long msgRandom){
        CgiRequest request = new CgiRequest();
        request.setUin(group.getGroupCode());
        request.setMsgSeq(msgSeq);
        request.setMsgRandom(msgRandom);
        send(new SendMsgBody(SendType.SEND_MSG, request));
    }
    /**
     * 主动退出群聊
     */
    public CommonlyResponseBody exitGroup(){
        CgiRequest request = new CgiRequest();
        request.setUin(group.getGroupCode());
        request.setOpCode(OptionType.EXIT_GROUP.getType());
        SendMsgBody body = new SendMsgBody(SendType.SSO_GROUP_OP, request);
        return send(body,CommonlyResponseBody.class);
    }
}
