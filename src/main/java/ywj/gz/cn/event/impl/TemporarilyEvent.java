package ywj.gz.cn.event.impl;

import lombok.Getter;
import org.springframework.lang.NonNull;
import ywj.gz.cn.body.pojo.*;
import ywj.gz.cn.body.receive.CommonlyResponseBody;
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

/**
 * 陌生人时间（临时事件）
 */
@Getter
public class TemporarilyEvent extends QQBotEvent {
    private final String content;
    private final Files video;
    private final Files voice;
    private final List<Files> images;
    private final Files file;
    private final String senderUid;
    private final Long senderQQ;
    private final GroupInfo groupInfo;
    public TemporarilyEvent(MessageData message, CompressImage compress, CacheImage cacheImage) {
        super(message.getCurrentQQ(),message.getCurrentPacket().getEventData().getMsgHead().getMsgUid(),compress,cacheImage);
        EventData data = message.getCurrentPacket().getEventData();
        this.video = data.getMsgBody().getVideo();
        this.file = data.getMsgBody().getFile();
        this.images = data.getMsgBody().getImages();
        this.voice = data.getMsgBody().getVoice();
        this.content = data.getMsgBody().getContent();
        this.senderQQ = data.getMsgHead().getSenderUin();
        this.groupInfo = data.getMsgHead().getGroupInfo();
        this.senderUid = data.getMsgHead().getSenderUid();
    }
    /**
     * 发送语音
     */
    public void sendVoice(@NonNull Files voice){
        sendMessage(null,null,voice,senderQQ);
    }
    /**
     * 发送图片
     */
    public void sendImage(@NonNull List<Files> images){
        sendMessage(null,images,null,senderQQ);
    }

    public void sendContent(@NonNull String text){
        sendMessage(text,null,null,senderQQ);
    }
    public void sendTemporarilyContent(@NonNull String text,long userQQ){
        sendMessage(text,null,null,userQQ);
    }
    /**
     * 可自定义构造消息组合
     */
    public void sendMessage(String text, List<Files> images, Files voiceBody,long userQQ){
        sendMsg(text,images,userQQ,voiceBody);
    }
    private void sendMsg(String content, List<Files> images, long userQQ,Files voiceBody){
        CgiRequest request = new CgiRequest();
        request.setToUin(userQQ);
        request.setGroupCode(groupInfo.getGroupCode());
        request.setToType(SourceType.TEMPORARILY.getType());
        request.setContent(content);
        request.setImages(images);
        request.setVoice(voiceBody);
        SendMsgBody msgBody = new SendMsgBody(SendType.SEND_MSG, request);
        send(msgBody, CommonlyResponseBody.class);
    }

    @Override
    public OptionType getOptionType() {
        return OptionType.NONE;
    }

    @Override
    public void setContent(String text) {
    }

    @Override
    protected void sendFriendImage(@NonNull File image, long userQQ) {
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
        sendToImage(files,senderQQ);
    }

    @Override
    protected void sendUrlImage(@NonNull String url) {
        Files files = null;
        try {
            files = uploadUrlImage(url);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToImage(files,senderQQ);
    }

    @Override
    protected void sendBaseImage(@NonNull String base64) {
        Files files = null;
        try {
            files = uploadBase64Image(base64);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToImage(files,senderQQ);
    }
    private void sendToImage(Files image,long userQQ){
        SendMsgBody body = msgBody(SourceType.FRIEND.getType(), null, userQQ, List.of(image), null, null);
        send(body);
    }
}
