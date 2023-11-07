package io.github.yangwanjun1.event;

import io.github.yangwanjun1.core.OpqRequest;
import io.github.yangwanjun1.data.*;
import lombok.Data;

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
