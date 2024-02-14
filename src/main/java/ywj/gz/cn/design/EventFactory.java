package ywj.gz.cn.design;

import ywj.gz.cn.body.pojo.MessageData;
import ywj.gz.cn.constants.SourceType;
import ywj.gz.cn.core.CacheImage;
import ywj.gz.cn.core.CompressImage;
import ywj.gz.cn.event.QQBotEvent;
import ywj.gz.cn.event.impl.*;
public class EventFactory {
    public static QQBotEvent instance(SourceType type, CacheImage cacheImage, CompressImage compress, MessageData message){
        return switch (type) {
            case FRIEND -> new FriendEvent(message, compress, cacheImage);
            case GROUP -> new GroupEvent(message, compress, cacheImage);
            case NOTICE -> new GroupNoticeEvent(message);
            case RED_BAG -> new RedBagEvent(message, compress, cacheImage);
            case FROM_REMOVE -> new ExitGroupEvent(message, compress, cacheImage);
            case FROM_INVITE -> new InviteEvent(message, compress, cacheImage);
            case TEMPORARILY -> new TemporarilyEvent(message, compress, cacheImage);
            case FRIEND_REQUEST -> new RequestEvent(message);
            default -> null;
        };
    }

    public static SourceType convertType(Integer type) {
        return switch (type) {
            case 1 -> SourceType.FRIEND;
            case 2 -> SourceType.GROUP;
            case 3 -> SourceType.TEMPORARILY;
            case 5 -> SourceType.NOTICE;
            case 11 -> SourceType.FRIEND_REQUEST;
            case 12 -> SourceType.RED_BAG;
            case 33 -> SourceType.FROM_INVITE;
            case 34 -> SourceType.FROM_REMOVE;
            default -> SourceType.NONE;
        };
    }
}
