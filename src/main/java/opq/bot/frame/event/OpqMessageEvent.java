package opq.bot.frame.event;

import lombok.Data;
import opq.bot.frame.core.OpqRequest;
import opq.bot.frame.data.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
public abstract class OpqMessageEvent implements OpqRequest {
    private long selfId;
    private GroupInfo group;
    private UserInfo userInfo;
    private LocalDateTime sendTime;
    private LocalDateTime msgTime;
    private RedBag redBag;
    private String content;
    private long msgId;
    private FileBody video;
    private FileBody voice;
    private List<FileBody> images;
    private List<AtUinLists> atUinLists;
}
