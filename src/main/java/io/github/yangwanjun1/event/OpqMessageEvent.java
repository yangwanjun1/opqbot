package io.github.yangwanjun1.event;

import io.github.yangwanjun1.core.OpqRequest;
import io.github.yangwanjun1.data.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
public abstract class OpqMessageEvent implements OpqRequest {
    private final long selfId;
    private final GroupInfo group;
    private final UserInfo userInfo;
    private final RedBag redBag;
    @Setter
    private String content;
    private final long msgId;
    private final FileBody video;
    private final FileBody voice;
    @Setter
    private List<FileBody> images;
    @Setter
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
