package io.github.yangwanjun1.event;

import io.github.yangwanjun1.core.OpqRequest;
import io.github.yangwanjun1.data.*;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.List;

@Data
public abstract class OpqMessageEvent implements OpqRequest {
    private long selfId;
    private GroupInfo group;
    private UserInfo userInfo;
    private RedBag redBag;
    private String content;
    private long msgId;
    private FileBody video;
    private FileBody voice;
    private List<FileBody> images;
    private List<AtUinLists> atUinLists;

    public OpqMessageEvent(EventData eventData, long currentQQ) {
        GroupInfo group = eventData.getMsgHead().getGroupInfo();
        this.userInfo = new UserInfo(eventData.getMsgHead().getSenderUin(), group == null ? null : group.getGroupCard());
        this.msgId = eventData.getMsgHead().getMsgUid();
        this.video = eventData.getMsgBody().getVideo();
        this.voice = eventData.getMsgBody().getVoice();
        this.images = eventData.getMsgBody().getImages();
        String content = eventData.getMsgBody().getContent();
        this.content = StringUtils.hasLength(content) ? content:null;
        this.atUinLists = eventData.getMsgBody().getAtUinLists();
        this.selfId = currentQQ;
        this.group = group;
        this.redBag = eventData.getMsgBody().getRedBag();
    }
}
