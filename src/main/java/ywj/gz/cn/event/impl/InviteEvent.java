package ywj.gz.cn.event.impl;

import lombok.Getter;
import org.springframework.lang.NonNull;
import ywj.gz.cn.body.pojo.*;
import ywj.gz.cn.body.send.SendMsgBody;
import ywj.gz.cn.constants.OptionType;
import ywj.gz.cn.constants.SourceType;
import ywj.gz.cn.core.CacheImage;
import ywj.gz.cn.core.CompressImage;
import ywj.gz.cn.event.QQBotEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 邀请进群事件（用户进群时会触发）
 */
@Getter
public class InviteEvent extends QQBotEvent {
    private final String invitee; //进群人(或被邀请人)uid
    private final String invitor;//邀请人(或处理人)uid
    private final Long groupId;
    public InviteEvent(MessageData message, CompressImage compress, CacheImage cacheImage) {
        super(message.getCurrentQQ(),message.getCurrentPacket().getEventData().getMsgHead().getMsgUid(),compress,cacheImage);
        EventData data = message.getCurrentPacket().getEventData();
        this.invitee = data.getEvent().getUid();
        this.invitor = data.getEvent().getAdminUid();
        this.groupId = data.getMsgHead().getFromUin();
    }

    public User getInviteeInfo() {
        return queryUserInfo(invitee);
    }

    public User getInvitorInfo() {
        return queryUserInfo(invitor);
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public void setContent(String text) {

    }

    public void sendContent(String text){
        sendMsg(text,null,null,null);
    }

    public void sendMsg(String content, List<Files> images, List<AtUinLists> at, Files voiceBody){
        SendMsgBody body = super.msgBody(SourceType.GROUP.getType(), content, groupId, images, at, voiceBody);
        send(body);
    }
    @Override
    public OptionType getOptionType() {
        return OptionType.GROUP_AGREE;
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
        SendMsgBody body = msgBody(SourceType.GROUP.getType(), null, groupId, List.of(image), null, null);
        send(body);
    }
}
