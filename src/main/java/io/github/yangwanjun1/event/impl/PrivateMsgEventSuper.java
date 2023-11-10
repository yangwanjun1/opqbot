package io.github.yangwanjun1.event.impl;

import io.github.yangwanjun1.data.EventData;
import io.github.yangwanjun1.data.FileBody;
import io.github.yangwanjun1.data.ResultData;
import io.github.yangwanjun1.event.OpqMessageEvent;
import io.github.yangwanjun1.utils.OpqUtils;

import java.util.List;

/**
 * 个人消息事件父类
 */
public abstract class PrivateMsgEventSuper extends OpqMessageEvent {

    public PrivateMsgEventSuper(EventData eventData, long currentQQ) {
        super(eventData,currentQQ);
    }

    public abstract int getType();
    /**
     * 回复用户
     */
    public ResultData replay(String content){
        return sendMsg(content,getUserInfo().getUserId());
    }

    /**
     * 发送给指定用户
     */
    public ResultData sendMsg(String content,long userId){
        String body = OpqUtils.msgBody(getType(),content,userId,null,null);
        return sendMsg(getSelfId(),body, ResultData.class);
    }

    /**
     * 发送（回复）图片【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendImage(List<FileBody> imageList){
        return sendImage(imageList,getUserInfo().getUserId());
    }

    /**
     * 发送图片给指定用户【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendImage(List<FileBody> imageList,long userId){
        return sendImage(null,imageList,userId);
    }

    /**
     * 发送(回复)图片【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendImage(FileBody data){
        return sendImage(data,getUserInfo().getUserId());
    }

    /**
     * 发送图片给用户【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendImage(FileBody data,long userId){
        return sendImage(null,data,userId);
    }

    /**
     * 发送(回复)图文【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendImage(String content,FileBody data){
        return sendImage(content,data,getUserInfo().getUserId());
    }

    public ResultData sendImage(String content,FileBody data,long userId){
        String result = msgBody(content,userId, List.of(data));
        return sendMsg(getSelfId(),result, ResultData.class);
    }
    /**
     * 发送(回复)图文列表给用户【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendImage(String content,List<FileBody> imageList){
        return sendImage(content,imageList,getUserInfo().getUserId());
    }

    /**
     * (回复)图文【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendImage(String content,List<FileBody> imageList,long userId){
        String body = msgBody(content,userId,imageList);
        return sendMsg(getSelfId(),body, ResultData.class);
    }
    public String msgBody(String content,long userId,List<FileBody> imageList){
        return OpqUtils.msgBody(getType(),content,userId,imageList, null);
    }
}
