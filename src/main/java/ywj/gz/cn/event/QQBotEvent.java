package ywj.gz.cn.event;


import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import ywj.gz.cn.body.pojo.AtUinLists;
import ywj.gz.cn.body.pojo.Files;
import ywj.gz.cn.body.pojo.User;
import ywj.gz.cn.body.receive.CommonlyResponseBody;
import ywj.gz.cn.body.receive.QueryUserBody;
import ywj.gz.cn.body.send.CgiRequest;
import ywj.gz.cn.body.send.SendMsgBody;
import ywj.gz.cn.constants.OptionType;
import ywj.gz.cn.constants.SendType;
import ywj.gz.cn.core.BotManager;
import ywj.gz.cn.core.CacheImage;
import ywj.gz.cn.core.CompressImage;
import ywj.gz.cn.util.MsgUtils;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;


public abstract class QQBotEvent {
    @Getter
    private final long selfId;
    @Getter
    private final long msgId;
    private final CompressImage compress;
    private final CacheImage cacheImage;
    public abstract OptionType getOptionType();
    public abstract String getContent();
    public abstract void setContent(String text);

    public QQBotEvent(long selfId, long msgId, CompressImage compress, CacheImage cacheImage){
        this.selfId = selfId;
        this.msgId = msgId;
        this.compress = compress;
        this.cacheImage = cacheImage;
    }
    /**
     * 构造请求并发送
     */
    private String request(String body) throws IOException, InterruptedException {
        String host = "http://"+BotManager.getHost(selfId)+"/v1/LuaApiCaller?funcname=MagicCgiCmd&timeout=10&qq="+selfId;
        HttpRequest request = MsgUtils.httpRequest(host, RequestMethod.POST,body);
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * 查询用户信息
     */
    protected User queryUserInfo(String uid){
        CgiRequest request = new CgiRequest();
        request.setUids(List.of(uid));
        SendMsgBody queryBody = new SendMsgBody(SendType.QUERY_UIN_BY_UID, request);
        QueryUserBody result = send(queryBody, QueryUserBody.class);
        return result == null || result.getResponseData().isEmpty() ? null : result.getResponseData().get(0);
    }

    /**
     * 发送消息
     */
    protected void send(Object bodyBean){
        try {
            request(MsgUtils.toJsonString(bodyBean));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    protected <T>T send(Object bodyBean,Class<T> cls){
        try {
            String request = request(MsgUtils.toJsonString(bodyBean));
            return MsgUtils.toBean(request,cls);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传群组文件
     * @param filePath 该路径应为linux格式的路径分隔符，否则可能导致失败
     */
    protected <T>T uploadFile(String filePath,long groupId,String fileName,Class<T> cls) throws IOException, InterruptedException {
        CgiRequest cgiRequest = new CgiRequest();
        cgiRequest.setCommandId(OptionType.COMMAND_ID.getType());
        cgiRequest.setFileName(fileName);
        cgiRequest.setFilePath(filePath);
        cgiRequest.setNotify(true);
        cgiRequest.setToUin(groupId);
        SendMsgBody msgBody = new SendMsgBody(SendType.DATA_UP_FILE, cgiRequest);
        String host = "http://"+ BotManager.getHost(selfId)+"/v1/upload?qq="+selfId;
        HttpRequest request = MsgUtils.httpRequest(host, RequestMethod.POST,MsgUtils.toJsonString(msgBody));
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return MsgUtils.toBean(response.body(),cls);
    }
    protected SendMsgBody msgBody(Integer toType, String content, long destination, List<Files> images, List<AtUinLists> at, Files voice){
        CgiRequest request = new CgiRequest();
        request.setToType(toType);
        request.setContent(content);
        request.setToUin(destination);
        request.setImages(images);
        request.setVoice(voice);
        request.setAtUinLists(at);
        return new SendMsgBody(SendType.SEND_MSG, request);
    }


    /**
     * 上传资源图片
     * @param filePath 文件路径
     */
    protected Files uploadImage(String filePath) throws IOException, InterruptedException {
        CgiRequest request = new CgiRequest();
        request.setCommandId(getOptionType().getType());
        request.setFilePath(filePath);
        return upload(request);
    }

    /**
     * 上传base64图片
     */
    protected Files uploadBase64Image(String base64) throws IOException, InterruptedException {
        CgiRequest request = new CgiRequest();
        request.setCommandId(getOptionType().getType());
        request.setBase64Buf(base64);
        return upload(request);
    }

    /**
     * 上传网络图片
     */
    protected Files uploadUrlImage(String url) throws IOException, InterruptedException {
        CgiRequest request = new CgiRequest();
        request.setCommandId(getOptionType().getType());
        request.setFileUrl(url);
        return upload(request);
    }


    protected void sendImage(@NonNull File image){}
    protected void sendUrlImage(@NonNull String url){}
    protected void sendBaseImage(@NonNull String base){}
    protected void sendFriendImage(@NonNull File image,long userQQ){}
    protected void sendFriendUrlImage(@NonNull String url,long userQQ){}
    protected void sendFriendBaseImage(@NonNull String base,long userQQ){}

    /**
     * 图片上传服务器(图片大的时候请使用base64压缩)
     */
    private Files upload(CgiRequest request) throws IOException, InterruptedException {
        if (request.getCommandId() == -1){
            return null;
        }
        if (Objects.nonNull(compress)){
            String base64 = null;
            if (StringUtils.hasText(request.getFilePath())){
                base64 = compress.compressFile(request.getFilePath());
            }else if (StringUtils.hasText(request.getBase64Buf())){
                base64 = compress.compressBase64(request.getBase64Buf());
            } else if (StringUtils.hasText(request.getFileUrl())) {
                base64 = compress.compressUrl(request.getFileUrl());
            }
            request.setBase64Buf(base64);
        }
        return uploadFile(request);
    }

    /**
     * 上传文件到服务器
     */
    private Files uploadFile(CgiRequest request) throws IOException, InterruptedException {
        SendMsgBody body = new SendMsgBody(SendType.DATA_UP_FILE, request);
        HttpRequest httpRequest = MsgUtils.httpRequest("http://"+ BotManager.getHost(selfId)+"/v1/upload?qq="+selfId,RequestMethod.POST,MsgUtils.toJsonString(body));
        HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
        CommonlyResponseBody bodyData = MsgUtils.toBean(response.body(), CommonlyResponseBody.class);
        Files files = new Files();
        files.setFileMd5(bodyData.getResponseData().getFileMd5());
        files.setFileSize(bodyData.getResponseData().getFileSize());
        files.setFileId(bodyData.getResponseData().getFileId());
        files.setWidth(7000);
        files.setHeight(15000);
        if (cacheImage != null && !StringUtils.hasText(request.getBase64Buf())){
            String path = request.getFilePath();
            String url = request.getFileUrl();
            cacheImage.cache(files,path,url);
        }
        return files;
    }
}
