package io.github.yangwanjun1.event.impl;

import io.github.yangwanjun1.data.EventData;
import io.github.yangwanjun1.data.FileBody;
import io.github.yangwanjun1.data.ResultData;
import io.github.yangwanjun1.event.OpqMessageEvent;
import io.github.yangwanjun1.utils.OpqUtils;

import java.io.File;
import java.util.List;

/**
 * 个人消息事件父类
 */
public abstract class PrivateMsgEventSuper extends OpqMessageEvent {

    public PrivateMsgEventSuper(EventData eventData, long currentQQ, Boolean photoCatch) {
        super(eventData,currentQQ, photoCatch);
    }

    public abstract int getType();
    /**
     * 回复用户
     */
    public ResultData replay(String content){
        return sendFriendMsg(content,getUserInfo().getUserId());
    }

    /**
     * 发送给指定用户
     */
    public ResultData sendFriendMsg(String content,long userId){
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
    public ResultData sendImage(File image){
        return sendImage(image,0.8);
    }
    public ResultData sendImage(File image, double f){
        return sendImage(image,getUserInfo().getUserId(),f);
    }

    /**
     * 发送图片给用户【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendImage(File image,long userId,double f){
        return sendImage(null,image,userId,f);
    }
    public ResultData sendImage(File image,long userId){
        return sendImage(null,image,userId,0.8);
    }

    /**
     * 发送(回复)图文【记得压缩图片，防止图片出现感叹号】
     */
    public ResultData sendImage(String content,File image){
        return sendImage(content,image,getUserInfo().getUserId(),0.8);
    }
    public ResultData sendImage(String content,File image,double f){
        return sendImage(content,image,getUserInfo().getUserId(),f);
    }

    public ResultData sendImage(String content,File image,long userId,double f){
        FileBody images = getImageCatch(image,f);
        String result = msgBody(content,userId, List.of(images));
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
