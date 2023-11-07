package io.github.yangwanjun1.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yangwanjun1.core.OpqRequest;
import io.github.yangwanjun1.data.*;
import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.*;

public class OpqUtils {


    @Getter
    private final static ObjectMapper mapper = new ObjectMapper();

    public static String toJsonString(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T>T toBean(String jsonString,Class<T> cls){
        try {
            return mapper.readValue(jsonString,cls);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean isAtMe(List<AtUinLists> atUinLists, long selfId) {
        if (Objects.isNull(atUinLists)) {
            return false;
        }
        return atUinLists.stream().anyMatch(a -> a.getUin() == selfId);
    }

    public static boolean isAtALL(List<AtUinLists> atUinLists) {
        if (Objects.isNull(atUinLists) || atUinLists.size() != 1) {
            return false;
        }
        return atUinLists.stream().anyMatch(a -> a.getNick().equals("全体成员"));
    }

    /**
     * @param toType      消息来源类型
     * @param content     内容
     * @param destination 发送到qq
     * @param images      图片
     */
    public static String msgBody(Integer toType, String content, Long destination, List<FileBody> images, List<AtUinLists> at) {
        SendDataBody body = new SendDataBody();
        body.setCgiRequest(new CgiRequest());
        body.getCgiRequest().setToType(toType);
        body.getCgiRequest().setContent(content);
        body.getCgiRequest().setToUin(destination);
        body.getCgiRequest().setImages(images);
        body.getCgiRequest().setAtUinLists(at);
        return toJsonString(body);
    }

    /**
     * @param url    图片网络地址
     * @param type   上传的资源类型
     * @param toBase 是否转base64
     */
    public static String uploadImageFileBody(String url, int type, boolean toBase) {
        FileData fileData = new FileData();
        fileData.setCgiRequest(new CgiRequest());
        fileData.getCgiRequest().setCommandId(type);
        if (toBase) {
            fileData.getCgiRequest().setBase64Buf(compress(url));
        } else {
            fileData.getCgiRequest().setFileUrl(url);
        }
        return toJsonString(fileData);
    }

    public static String uploadImageFileBody(File file, int type) {
        FileData fileData = new FileData();
        fileData.setCgiRequest(new CgiRequest());
        fileData.getCgiRequest().setCommandId(type);
        fileData.getCgiRequest().setBase64Buf(compress(file));
        return toJsonString(fileData);
    }

    /**
     * 压缩图片
     *
     * @param url 网络地址
     */
    public static String compress(String url) {
        try (ByteArrayOutputStream boas = new ByteArrayOutputStream()) {
            Request posted = Request.get(url);
            Response response = posted.execute();
            if (response.returnResponse().getCode() != 200) {
                throw new RuntimeException(response.returnContent().asString());
            }
            ByteArrayInputStream bois = new ByteArrayInputStream(response.returnContent().asBytes());
            Thumbnails.of(bois).scale(1).outputQuality(0.8).toOutputStream(boas);
            bois.close();
            return Base64.getEncoder().encodeToString(boas.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String compress(File file) {
        try (ByteArrayOutputStream boas = new ByteArrayOutputStream()) {
            Thumbnails.of(file).scale(1).outputQuality(0.8).toOutputStream(boas);
            return Base64.getEncoder().encodeToString(boas.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<AtUinLists> atUinLists(long userId, String groupCard) {
        return atUinLists(Map.of(userId, groupCard));
    }

    public static List<AtUinLists> atUinLists(Map<Long, String> atUserMap) {
        return Optional.ofNullable(atUserMap)
                .orElse(Collections.emptyMap())
                .entrySet().stream()
                .map((e) -> new AtUinLists(e.getValue(), null, e.getKey()))
                .toList();
    }


    /**
     * 撤回群消息
     */
    public static FileData revocation(long msgSeq,long msgRandom,long groupId){
        FileData data = new FileData();
        data.setCgiCmd("SsoGroup.Op");
        data.setCgiRequest(new CgiRequest());
        data.getCgiRequest().setOpCode(4691L);
        data.getCgiRequest().setUin(groupId);
        data.getCgiRequest().setMsgSeq(msgSeq);
        data.getCgiRequest().setMsgRandom(msgRandom);
        return data;
    }
    /**
     * 禁言
     */
    public static FileData ban(long groupId,String uid,Integer time){
        FileData data = new FileData();
        data.setCgiCmd("SsoGroup.Op");
        data.setCgiRequest(new CgiRequest());
        data.getCgiRequest().setOpCode(4691L);
        data.getCgiRequest().setUin(groupId);
        data.getCgiRequest().setUid(uid);
        data.getCgiRequest().setBanTime(time);
        return data;
    }
    /**
     * 踢成员
     */
    public static FileData eliminate(long groupId,String uid){
        FileData data = new FileData();
        data.setCgiCmd("SsoGroup.Op");
        data.setCgiRequest(new CgiRequest());
        data.getCgiRequest().setOpCode(2208L);
        data.getCgiRequest().setUin(groupId);
        data.getCgiRequest().setUid(uid);
        return data;
    }
    /**
     * 退出群聊
     */
    public static FileData leaveTheGroupBody(long groupId){
        FileData data = new FileData();
        data.setCgiCmd("SsoGroup.Op");
        data.setCgiRequest(new CgiRequest());
        data.getCgiRequest().setOpCode(4247L);
        data.getCgiRequest().setUin(groupId);
        return data;
    }
    /**
     * 获取uin
     */
    public static QueryUinBody queryUin(String uid){
        QueryUinBody queryUinBody = new QueryUinBody();
        CgiRequest request = new CgiRequest();
        request.setUid(uid);
        queryUinBody.setCgiRequest(request);
        return queryUinBody;
    }


}
