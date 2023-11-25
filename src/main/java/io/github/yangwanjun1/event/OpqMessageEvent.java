package io.github.yangwanjun1.event;

import io.github.yangwanjun1.constants.OptionType;
import io.github.yangwanjun1.core.OpqRequest;
import io.github.yangwanjun1.data.*;
import io.github.yangwanjun1.utils.OpqUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Getter
public abstract class OpqMessageEvent implements OpqRequest {
    private final long selfId;
    private final GroupInfo group;
    private final UserInfo userInfo;
    private final RedBag redBag;
    @Setter
    private String content;
    private final Long msgId;
    private final FileBody video;
    private final FileBody voice;
    @Setter
    private List<FileBody> images;
    @Setter
    private List<AtUinLists> atUinLists;
    private final FileData file;
    private final boolean photoCatch;

    public OpqMessageEvent(EventData eventData, long currentQQ, boolean photoCatch) {
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
        this.file = eventData.getMsgBody().getFile();
        this.photoCatch = photoCatch;
    }
    protected FileBody getImageCatch(File files, double f){
        return imageCatch(files,f);
    }
    private FileBody imageCatch(File files,double f){
        FileBody image = null;
        try {
            GroupInfo groupInfo = getGroup();
            if (photoCatch) {
                image =  OpqUtils.getCatchImage(files.getName());
            }
            if (image == null){
                String base = OpqUtils.compress(files, f);
                image = OpqUtils.fileBody(null, base, null, groupInfo == null ?OptionType.FRIEND_IMAGE: OptionType.GROUP_IMAGE, getSelfId(), groupInfo == null ?null: groupInfo.getGroupCode());
            }
            if (photoCatch){
                OpqUtils.imageCatchPut(files.getName(),image);
            }
            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
