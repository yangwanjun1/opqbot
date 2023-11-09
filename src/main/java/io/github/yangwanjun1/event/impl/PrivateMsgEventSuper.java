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
     * 发送图片
     */
    public ResultData sendImage(List<FileBody> imageList){
        return sendImage(imageList,getUserInfo().getUserId());
    }

    /**
     * 发送图片给指定用户(q压缩质量，1不压缩)
     */
    public ResultData sendImage(List<FileBody> imageList,long userId){
        return sendImage(null,imageList,userId);
    }

    /**
     * 发送网络图片(q压缩质量，1不压缩)
     */
    public ResultData sendImage(String url,double q){
        return sendImage(url,getUserInfo().getUserId(),q);
    }
    public ResultData sendImage(String url,long userId,double q){
        return sendImage(null,url,userId,q);
    }

    /**
     * 发送图文给用户(q压缩质量，1不压缩)
     */
    public ResultData sendImage(String content,String url,long userId,double q){
        String body =  OpqUtils.uploadImageFileBody(url, getType(), true,q);
        FileBody data = uploadImageFile(getSelfId(), body);
        String result = msgBody(content,userId,List.of(data));
        return sendMsg(getSelfId(), result, ResultData.class);
    }

    /**
     * 发送本地图片(q压缩质量，1不压缩)
     */
    public ResultData sendImage(File file,double q){
        return sendImage(file,getUserInfo().getUserId(),q);
    }
    public ResultData sendImage(File file,long userId,double q){
        return sendImage(null,file,userId,q);
    }

    /**
     * 发送图文(q压缩质量，1不压缩)
     */
    public ResultData sendImage(String content,File file,double q){
        return sendImage(content,file,getUserInfo().getUserId(),q);
    }

    public ResultData sendImage(String content,File file,long userId,double q){
        String body = OpqUtils.uploadImageFileBody(file, getType(),q);
        FileBody data = uploadImageFile(getSelfId(), body);
        String result = msgBody(content,userId, List.of(data));
        return sendMsg(getSelfId(),result, ResultData.class);
    }
    /**
     * 发送图文列表给用户
     */
    public ResultData sendImage(String content,List<FileBody> imageList){
        return sendImage(content,imageList,getUserInfo().getUserId());
    }
    public ResultData sendImage(String content,List<FileBody> imageList,long userId){
        String body = msgBody(content,userId,imageList);
        return sendMsg(getSelfId(),body, ResultData.class);
    }
    public String msgBody(String content,long userId,List<FileBody> imageList){
        return OpqUtils.msgBody(getType(),content,userId,imageList, null);
    }
}
