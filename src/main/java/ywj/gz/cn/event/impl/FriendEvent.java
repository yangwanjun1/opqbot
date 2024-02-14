package ywj.gz.cn.event.impl;

import lombok.Getter;
import org.springframework.lang.NonNull;
import ywj.gz.cn.body.pojo.*;
import ywj.gz.cn.body.receive.FriendBody;
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
public class FriendEvent extends QQBotEvent {
    private final String content;
    private final Files video;
    private final Files voice;
    private final List<Files> images;
    private final Files file;
    private final String friendUid;
    private final Long friendQQ;

    public FriendEvent(MessageData message, CompressImage compress, CacheImage cacheImage) {
        super(message.getCurrentQQ(),message.getCurrentPacket().getEventData().getMsgHead().getMsgUid(),compress,cacheImage);
        EventData data = message.getCurrentPacket().getEventData();
        this.video = data.getMsgBody().getVideo();
        this.file = data.getMsgBody().getFile();
        this.images = data.getMsgBody().getImages();
        this.voice = data.getMsgBody().getVoice();
        this.content = data.getMsgBody().getContent();
        this.friendQQ = data.getMsgHead().getSenderUin();
        this.friendUid = data.getMsgHead().getSenderUid();
    }
    public User getUserInfo() {
        return queryUserInfo(friendUid);
    }
    /**
     * 发送图片
     */
    public void sendImage(@NonNull List<Files> images){
        sendMessage(null,images);
    }

    public void sendContent(@NonNull String text){
        sendMessage(text,null);
    }
    /**
     * 可自定义构造消息组合
     */
    public void sendMessage(String text, List<Files> images){
        sendMsg(text,images,friendQQ);
    }
    public void sendFriendMessage(String text, List<Files> images,long userQQ){
        sendMsg(text,images,userQQ);
    }
    public void sendFriendMessage(List<Files> images,long userQQ){
        sendMsg(null,images,userQQ);
    }
    public void sendFriendMessage(String text,long userQQ){
        sendMsg(text,null,userQQ);
    }
    private void sendMsg(String content, List<Files> images,long userQQ){
        CgiRequest request = new CgiRequest();
        request.setToType(SourceType.FRIEND.getType());
        request.setContent(content);
        request.setToUin(userQQ);
        request.setImages(images);
        request.setVoice(voice);
        SendMsgBody body = new SendMsgBody(SendType.SEND_MSG,request);
        send(body);
    }
    @Override
    public OptionType getOptionType() {
        return OptionType.FRIEND_IMAGE;
    }

    @Override
    public void setContent(String text) {

    }

    @Override
    protected void sendFriendImage(@NonNull File image,long userQQ) {
        Files files = null;
        try {
            files = uploadImage(image.getAbsolutePath());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToImage(files,userQQ);
    }

    @Override
    protected void sendFriendUrlImage(@NonNull String url,long userQQ) {
        Files files = null;
        try {
            files = uploadUrlImage(url);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToImage(files,userQQ);
    }

    @Override
    protected void sendFriendBaseImage(@NonNull String base64,long userQQ) {
        Files files = null;
        try {
            files = uploadBase64Image(base64);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToImage(files,userQQ);
    }
    @Override
    protected void sendImage(@NonNull File image) {
        Files files = null;
        try {
            files = uploadImage(image.getAbsolutePath());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToImage(files,friendQQ);
    }

    @Override
    protected void sendUrlImage(@NonNull String url) {
        Files files = null;
        try {
            files = uploadUrlImage(url);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToImage(files,friendQQ);
    }

    @Override
    protected void sendBaseImage(@NonNull String base64) {
        Files files = null;
        try {
            files = uploadBase64Image(base64);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToImage(files,friendQQ);
    }
    private void sendToImage(Files image,long userQQ){
        SendMsgBody body = msgBody(SourceType.FRIEND.getType(), null, userQQ, List.of(image), null, null);
        send(body);
    }

    public FriendBody friendList(Long lastUin){
        CgiRequest request = new CgiRequest();
        request.setLastUin(lastUin);
        SendMsgBody body = new SendMsgBody(SendType.FRIEND_LIST, request);
        return send(body,FriendBody.class);
    }

    public FriendBody friendList(){
        return friendList(0L);
    }

    /**
     * 获取分组
     */
    public List<Tag> tagList(){
        return friendList(0L).getResponseData().getTagLists();
    }
}

