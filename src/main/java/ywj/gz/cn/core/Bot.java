package ywj.gz.cn.core;


import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import ywj.gz.cn.body.pojo.AtUinLists;
import ywj.gz.cn.body.pojo.Files;
import ywj.gz.cn.body.receive.CgiBaseResponse;
import ywj.gz.cn.body.receive.CommonlyResponseBody;
import ywj.gz.cn.body.receive.ResponseData;
import ywj.gz.cn.body.send.CgiRequest;
import ywj.gz.cn.body.send.SendMsgBody;
import ywj.gz.cn.constants.OptionType;
import ywj.gz.cn.constants.SendType;
import ywj.gz.cn.constants.SourceType;
import ywj.gz.cn.util.MsgUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;

public record Bot(String host, long selfId,CompressImage compress,CacheImage cacheImage) {

    /**
     * 发送朋友消息
     */
    public void sendFriendMsg(String text,long qq){
        sendFriendMsg(null,text,qq);
    }
    public void sendFriendMsg(Files image, long qq){
        sendFriendMsg(List.of(image),null ,qq);
    }
    public void sendFriendMsg(String text,Files image, long qq){
        sendFriendMsg(List.of(image),text ,qq);
    }
    public void sendFriendMsg(List<Files> images, long qq){
        sendFriendMsg(images,null,qq);
    }
    public void sendFriendMsg(String text,List<Files> images,  long qq){
        sendFriendMsg(images,text,qq);
    }
    public void sendFriendMsg(List<Files> images,String text, long qq) throws RuntimeException {
        CgiRequest cgiRequest = new CgiRequest();
        cgiRequest.setToUin(qq);
        cgiRequest.setToType(SourceType.FRIEND.getType());
        cgiRequest.setContent(text);
        cgiRequest.setImages(images);
        SendMsgBody body = new SendMsgBody(SendType.SEND_MSG, cgiRequest);
        try {
            request(MsgUtils.toJsonString(body));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传图片(上传之后拿到files，才可以发送图片)
     * @param filepath 本地文件路径 linux格式
     * @param base64 base64类型
     * @param url 网络图片
     * @return opq图片
     * 以上参数 三选一 不可同时存在
     * @param optionType 上传的资源类型
     */
    public Files updateImage(String filepath,String base64,String url, OptionType optionType) throws IOException, InterruptedException {
        CgiRequest request = new CgiRequest();
        request.setCommandId(optionType.getType());
        request.setBase64Buf(base64);
        request.setFileUrl(url);
        request.setFilePath(filepath);
        if (request.getCommandId() == -1){
            return null;
        }
        if (Objects.nonNull(compress)){
            if (StringUtils.hasText(request.getFilePath())){
                request.setBase64Buf(compress.compressFile(request.getFilePath()));
            }else if (StringUtils.hasText(request.getBase64Buf())){
                request.setBase64Buf(compress.compressFile(request.getBase64Buf()));
            } else if (StringUtils.hasText(request.getFileUrl())) {
                request.setBase64Buf(compress.compressFile(request.getFileUrl()));
            }
        }
        SendMsgBody body = new SendMsgBody(SendType.DATA_UP_FILE, request);
        HttpRequest httpRequest = MsgUtils.httpRequest("http://"+ host+"/v1/upload?qq="+selfId,RequestMethod.POST,MsgUtils.toJsonString(body));
        HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
        CommonlyResponseBody bodyData = MsgUtils.toBean(response.body(), CommonlyResponseBody.class);
        Files files = new Files();
        files.setFileMd5(bodyData.getResponseData().getFileMd5());
        files.setFileSize(bodyData.getResponseData().getFileSize());
        files.setFileId(bodyData.getResponseData().getFileId());
        files.setWidth(7000);
        files.setHeight(15000);
        if (cacheImage != null && !StringUtils.hasText(request.getBase64Buf())){
            cacheImage.cache(files,request.getFilePath(),request.getFileUrl());
        }
        return files;
    }
    /**
     * =============================================================================
     * 发送群聊消息
     */
    public void sendGroupMessage(String text, long groupId){
        sendGroupMessage(null,text,groupId,null);
    }
    public void sendGroupMessage(Files image, long groupId){
        sendGroupMessage(List.of(image),null,groupId,null);
    }
    public void sendGroupMessage(String text, Files image, long groupId){
        sendGroupMessage(List.of(image),text,groupId,null);
    }
    public void sendGroupMessage(List<Files> image, long groupId){
        sendGroupMessage(image,null,groupId,null);
    }

    /**
     * ====================================================================
     * 群消息at
     */

    public void sendGroupAtMsg(String text,long groupId,long userQQ){
        sendGroupMessage(null,text,groupId,userQQ);
    }
    public void sendGroupAtMsg(Files image,long groupId,long userQQ){
        sendGroupMessage(List.of(image),null,groupId,userQQ);
    }
    public void sendGroupAtMsg(String text, Files image,long groupId,long userQQ){
        sendGroupMessage(List.of(image),text,groupId,userQQ);
    }
    public void sendGroupAtMsg(List<Files> image,long groupId,long userQQ){
        sendGroupMessage(image,null,groupId,userQQ);
    }

    public void sendGroupMessage(List<Files> image, String text, long groupId, Long userQQ){
        CgiRequest cgiRequest = new CgiRequest();
        cgiRequest.setToUin(groupId);
        if (userQQ != null){
            cgiRequest.setAtUinLists(List.of(new AtUinLists("",null,userQQ)));
        }
        cgiRequest.setToType(SourceType.GROUP.getType());
        cgiRequest.setContent(text);
        cgiRequest.setImages(image);
        SendMsgBody body = new SendMsgBody(SendType.SEND_MSG, cgiRequest);
        try {
            request(MsgUtils.toJsonString(body));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 踢本群用户（需要是管理员）
     */
    public CgiBaseResponse removeUser(@NonNull String uid,long groupId){
        try {
            CgiRequest request = new CgiRequest();
            request.setOpCode(OptionType.REMOVE_GROUPER.getType());
            request.setUin(groupId);
            request.setUid(uid);
            SendMsgBody body = new SendMsgBody(SendType.SSO_GROUP_OP, request);
            String bodyData = request(MsgUtils.toJsonString(body));
            CommonlyResponseBody bean = MsgUtils.toBean(bodyData, CommonlyResponseBody.class);
            return bean.getCgiBaseResponse();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 禁言（需要是管理员）
     * 单位秒 至少60秒 至多30天 禁言一天为24*3600=86400 参数为0解除禁言
     */
    public CgiBaseResponse banUser(@NonNull String uid,int time,long groupId){
        if (time < 60){
            throw new RuntimeException("至少禁言一分钟");
        }
        try {
            CgiRequest request = new CgiRequest();
            request.setOpCode(OptionType.BEN_GROUPER.getType());
            request.setUin(groupId);
            request.setUid(uid);
            request.setBanTime(time);
            SendMsgBody body = new SendMsgBody(SendType.SSO_GROUP_OP, request);
            String bodyData  = request(MsgUtils.toJsonString(body));
            CommonlyResponseBody send = MsgUtils.toBean(bodyData, CommonlyResponseBody.class);
            return send.getCgiBaseResponse();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取群成员列表
     */
    public ResponseData groupMemberList(@NonNull String lastBuffer,long groupId){
        try {
            CgiRequest request = new CgiRequest();
            request.setUin(groupId);
            request.setLastBuffer(lastBuffer);
            SendMsgBody body = new SendMsgBody(SendType.GROUP_MEMBER_LIST,request);
            String bodyData = request(MsgUtils.toJsonString(body));
            CommonlyResponseBody send = MsgUtils.toBean(bodyData, CommonlyResponseBody.class);
            return send.getResponseData();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    public ResponseData groupMemberList(long groupId){
        return groupMemberList("",groupId);
    }

    /**
     * 上传群组文件(使用linux文件分隔符 / )
     * @param filePath 文件的绝对路径
     * @param fileName 文件名
     */
    public CgiBaseResponse uploadFile(String filePath, long groupId, String fileName) throws IOException, InterruptedException {
        return uploadFileData(filePath,groupId,fileName);
    }
    private CgiBaseResponse uploadFileData(String filePath,long groupId,String fileName) throws IOException, InterruptedException {
        CgiRequest cgiRequest = new CgiRequest();
        cgiRequest.setCommandId(OptionType.COMMAND_ID.getType());
        cgiRequest.setFileName(fileName);
        cgiRequest.setFilePath(filePath);
        cgiRequest.setNotify(true);
        cgiRequest.setToUin(groupId);
        SendMsgBody msgBody = new SendMsgBody(SendType.DATA_UP_FILE, cgiRequest);
        HttpRequest request = MsgUtils.httpRequest("http://"+ host+"/v1/upload?qq="+selfId, RequestMethod.POST,MsgUtils.toJsonString(msgBody));
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return MsgUtils.toBean(response.body(),CgiBaseResponse.class);
    }

    /**
     * 撤回消息
     */
    public void withdrawMsg(long msgSeq,long msgRandom,long groupId){
        CgiRequest request = new CgiRequest();
        request.setUin(groupId);
        request.setMsgSeq(msgSeq);
        request.setMsgRandom(msgRandom);
        try {
            request(MsgUtils.toJsonString(new SendMsgBody(SendType.SEND_MSG, request)));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 主动退出群聊
     */
    public CommonlyResponseBody exitGroup(long groupId){
        CgiRequest request = new CgiRequest();
        request.setUin(groupId);
        request.setOpCode(OptionType.EXIT_GROUP.getType());
        SendMsgBody body = new SendMsgBody(SendType.SSO_GROUP_OP, request);
        try {
            String bodyData = request(MsgUtils.toJsonString(body));
            return MsgUtils.toBean(bodyData, CommonlyResponseBody.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构造请求
     */
    private String request(String body) throws IOException, InterruptedException {
        HttpRequest request = MsgUtils.httpRequest("http://"+host+"/v1/LuaApiCaller?funcname=MagicCgiCmd&timeout=10&qq="+selfId, RequestMethod.POST,body);
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
