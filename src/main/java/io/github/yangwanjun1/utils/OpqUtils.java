package io.github.yangwanjun1.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luciad.imageio.webp.WebPReadParam;
import io.github.yangwanjun1.constants.OptionType;
import io.github.yangwanjun1.core.OpqWebSocket;
import io.github.yangwanjun1.data.*;
import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.util.Timeout;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
     *
     * @param url 网络地址
     * @param type 发送事件类型
     * @param selfId 机器人id
     * [不建议直接发送图片，可能会导致图片不可用，建议压缩（直接转base64可能图片不可用）之后使用]
     */
    public static FileBody fileBodyUrl(String url, OptionType type,long selfId) {
        return fileBody(url,null,null,type,selfId);
    }
    /**
     *
     * @param base64 图片base64
     * @param type 发送事件类型
     * @param selfId 机器人id
     * [不建议直接发送图片，可能会导致图片不可用，建议压缩（直接转base64可能图片不可用）之后使用]
     */
    public static FileBody fileBodyBase64(String base64, OptionType type,long selfId) {
        return fileBody(null,base64,null,type,selfId);
    }
    /**
     *
     * @param filePath 本地图片路径
     * @param type 发送事件类型
     * @param selfId 机器人id
     * [不建议直接发送图片，可能会导致图片不可用，建议压缩（直接转base64可能图片不可用）之后使用]
     */
    public static FileBody fileBodyFilePath(String filePath, OptionType type,long selfId) {
        return fileBody(null,null,filePath,type,selfId);
    }

    /**
     * 原始方法，不进行任何压缩
     * [不建议直接发送图片，可能会导致图片不可用，建议压缩（直接转base64可能可能图片不可用）之后使用]
     * @param url 网络资源
     * @param base64 base64
     * @param filePath 本地路径
     *  [三选一]
     * @param type 发送到群或者私人
     * @param selfId 机器人id
     */
    public static FileBody fileBody(String url,String base64,String filePath, OptionType type,long selfId) {
        FileData fileData = new FileData();
        fileData.setCgiRequest(new CgiRequest());
        fileData.getCgiRequest().setCommandId(type.getType());
        fileData.getCgiRequest().setFileUrl(url);
        fileData.getCgiRequest().setFilePath(filePath);
        fileData.getCgiRequest().setBase64Buf(base64);
        return uploadImageFile(toJsonString(fileData),selfId);
    }

    private static FileBody uploadImageFile(String body,long selfId) {
        Request posted = Request.post("http://" + OpqWebSocket.getHost() + "/v1/upload?timeout=30&qq=" + selfId);
        try {
            posted.bodyString(body, ContentType.APPLICATION_JSON);
            posted.connectTimeout(Timeout.ofMinutes(1));
            posted.responseTimeout(Timeout.ofMinutes(1));
            Response response = posted.execute();
            String string = response.returnContent().asString(StandardCharsets.UTF_8);
            ResultData data = OpqUtils.toBean(string, ResultData.class);
            return fileBody(data.getResponseData().getFileId(),data.getResponseData().getFileMd5(),data.getResponseData().getFileSize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static FileBody fileBody(Long fileId,String fileMd5,long fileSize){
        FileBody body = new FileBody();
        body.setFileId(fileId);
        body.setFileMd5(fileMd5);
        body.setFileSize(fileSize);
        body.setWidth(3000);
        body.setHeight(2000);
        return body;
    }

    /**
     * 构建at体
     */
    public static List<AtUinLists> atUinLists(long userId, String groupCard) {
        return atUinLists(Map.of(userId, groupCard));
    }

    /**
     * key：qq号 value：昵称（可忽略）
     */
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
        data.getCgiRequest().setOpCode(4691);
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
        data.getCgiRequest().setOpCode(4691);
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
        data.getCgiRequest().setOpCode(2208);
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
        data.getCgiRequest().setOpCode(4247);
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

    /**
     * 压缩图片(q：压缩后的质量，>1原比例)
     */
    public static String compress(File file, double q) throws IOException {
        try (ByteArrayOutputStream boas = new ByteArrayOutputStream()) {
            Thumbnails.of(file).scale(q).toOutputStream(boas);
            return Base64.getEncoder().encodeToString(boas.toByteArray());
        } catch (IOException e) {
            return compressWebp(file,q);
        }
    }

    /**
     * 压缩图片(q：压缩后的质量，>1=原比例)
     * @param file 下载到的图片
     */
    public static String compress(byte[] file, double q) throws IOException {
        try (ByteArrayOutputStream boas = new ByteArrayOutputStream()) {
            ByteArrayInputStream bios = new ByteArrayInputStream(file);
            Thumbnails.of(bios).scale(q).toOutputStream(boas);
            bios.close();
            return Base64.getEncoder().encodeToString(boas.toByteArray());
        } catch (IOException e) {
           throw new IOException(e.getMessage());
        }
    }


    /**
     * webp格式压缩
     */
    public static String compressWebp(File file, double q) throws IOException {
        ImageReader next = ImageIO.getImageReadersByMIMEType("image/webp").next();
        WebPReadParam wpp = new WebPReadParam();
        wpp.setBypassFiltering(true);
        FileImageInputStream input = new FileImageInputStream(file);
        next.setInput(input);
        BufferedImage image = next.read(0, wpp);
        File output = new File(file.getAbsolutePath());
        boolean jpg = ImageIO.write(image, "jpg", output);
        if (jpg && file.delete()) {
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            Thumbnails.of(file).size(image.getWidth(), image.getHeight()).scale(q).toOutputStream(boas);
            return Base64.getEncoder().encodeToString(boas.toByteArray());
        }
        throw new IOException("不支持的格式");
    }
}
