package io.github.yangwanjun1.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luciad.imageio.webp.WebPReadParam;
import io.github.yangwanjun1.constants.OptionCode;
import io.github.yangwanjun1.constants.OptionType;
import io.github.yangwanjun1.constants.SendType;
import io.github.yangwanjun1.core.WsServerSocket;
import io.github.yangwanjun1.core.WsSocketClient;
import io.github.yangwanjun1.data.*;
import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;

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

    private static final Map<String,FileBody> imageCatch = new HashMap<>(8);
    public static FileBody getCatchImage(String filename){
        return imageCatch.get(filename);
    }
    public static void imageCatchPut(String filename,FileBody image){
        imageCatch.put(filename,image);
    }
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
        return atUinLists.stream().anyMatch(a -> a.getUin() == selfId && a.getUin() != 0);
    }
    public static boolean isAtALL(List<AtUinLists> atUinLists) {
        if (Objects.isNull(atUinLists) || atUinLists.size() != 1) {
            return false;
        }
        return atUinLists.stream().anyMatch(a -> a.getNick().equals("全体成员") && a.getUin() == 0);
    }

    /**
     * @param toType      消息来源类型
     * @param content     内容
     * @param destination 发送到qq
     * @param images      图片
     */
    public static String msgBody(Integer toType, String content, Long destination, List<FileBody> images, List<AtUinLists> at) {
        SendMsgBody body = new SendMsgBody(SendType.SEND_MSG,new CgiRequest());
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
    public static FileBody fileBodyUrl(String url, OptionType type,long selfId,Long uin) {
        return fileBody(url,null,null,type,selfId,uin);
    }
    /**
     *
     * @param base64 图片base64
     * @param type 发送事件类型
     * @param selfId 机器人id
     * [不建议直接发送图片，可能会导致图片不可用，建议压缩（直接转base64可能图片不可用）之后使用]
     */
    public static FileBody fileBodyBase64(String base64, OptionType type,long selfId,Long uin) {
        return fileBody(null,base64,null,type,selfId,uin);
    }
    /**
     *
     * @param filePath 本地图片路径
     * @param type 发送事件类型
     * @param selfId 机器人id
     * [不建议直接发送图片，可能会导致图片不可用，建议压缩（直接转base64可能图片不可用）之后使用]
     */
    public static FileBody fileBodyFilePath(String filePath, OptionType type,long selfId,Long uin) {
        return fileBody(null,null,filePath,type,selfId,uin);
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
    public static FileBody fileBody(String url,String base64,String filePath, OptionType type,long selfId,Long uin) {
        SendMsgBody fileData = new SendMsgBody(SendType.DATA_UP_FILE,new CgiRequest());
        fileData.getCgiRequest().setCommandId(type.getType());
        fileData.getCgiRequest().setFileUrl(url);
        fileData.getCgiRequest().setToUin(uin);
        fileData.getCgiRequest().setFilePath(filePath);
        fileData.getCgiRequest().setBase64Buf(base64);
        return uploadImageFile(toJsonString(fileData),selfId);
    }

    private static FileBody uploadImageFile(String body,long selfId) {
        String hosts = WsSocketClient.getHost();
        String format = hosts == null ? WsServerSocket.getHost(selfId) : hosts;
        String uri = "http://" + format + "/v1/upload?timeout=30&qq=" + selfId;
        Request posted = Request.post(uri);
        try {
            posted.bodyString(body, ContentType.APPLICATION_JSON);
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
        body.setHeight(2500);
        return body;
    }

    /**
     * 构建at体
     */
    public static List<AtUinLists> atUinLists(Long userId, String groupCard) {
        return userId == null ? null:atUinLists(Map.of(userId, groupCard));
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
    public static SendMsgBody revocation(long msgSeq,long msgRandom,long groupId){
        SendMsgBody data = new SendMsgBody(SendType.GROUP_REMOVE_MSG,new CgiRequest());
        data.getCgiRequest().setUin(groupId);
        data.getCgiRequest().setMsgSeq(msgSeq);
        data.getCgiRequest().setMsgRandom(msgRandom);
        return data;
    }
    /**
     * 禁言
     */
    public static SendMsgBody ban(long groupId,String uid,Integer time){
        SendMsgBody data = new SendMsgBody(SendType.SSO_GROUP_OP,new CgiRequest());
        data.getCgiRequest().setOpCode(OptionCode.BEN_GROUPER.getCode());
        data.getCgiRequest().setUin(groupId);
        data.getCgiRequest().setUid(uid);
        data.getCgiRequest().setBanTime(time);
        return data;
    }
    /**
     * 踢成员
     */
    public static SendMsgBody eliminate(long groupId,String uid){
        SendMsgBody ban = ban(groupId, uid, null);
        ban.getCgiRequest().setOpCode(OptionCode.REMOVE_GROUPER.getCode());
        return ban;
    }
    /**
     * 退出群聊
     */
    public static SendMsgBody leaveTheGroupBody(long groupId){
        SendMsgBody ban = ban(groupId, null, null);
        ban.getCgiRequest().setOpCode(OptionCode.EXIT_GROUP.getCode());
        return ban;
    }
    /**
     * 获取uin
     */
    public static SendMsgBody queryUin(String uid){
        SendMsgBody body = new SendMsgBody(SendType.QUERY_UIN,new CgiRequest());
        body.getCgiRequest().setUid(uid);
        return body;
    }

    /**
     * 压缩图片(q：压缩后的质量，=1原比例)
     */
    public static String compress(File file, double q) throws IOException {
        if (getImageFormat(new FileInputStream(file)).equals("Unknown")){
            return compressWebp(file,q);
        }
        try (ByteArrayOutputStream boas = new ByteArrayOutputStream()) {
            Thumbnails.of(file).scale(q).outputQuality(1).toOutputStream(boas);
            return Base64.getEncoder().encodeToString(boas.toByteArray());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 判断图片格式
     */
    public static String getImageFormat(FileInputStream inputStream) throws IOException {
        byte[] buffer = new byte[8];
        inputStream.read(buffer);
        inputStream.close();
        if (buffer[0] == (byte) 0xFF && buffer[1] == (byte) 0xD8) {
            return "JPEG|JPG";
        } else if (buffer[0] == (byte) 0x89 && buffer[1] == (byte) 0x50 && buffer[2] == (byte) 0x4E && buffer[3] == (byte) 0x47
                && buffer[4] == (byte) 0x0D && buffer[5] == (byte) 0x0A && buffer[6] == (byte) 0x1A && buffer[7] == (byte) 0x0A) {
            return "PNG";
        } else if (buffer[0] == (byte) 0x47 && buffer[1] == (byte) 0x49 && buffer[2] == (byte) 0x46 && buffer[3] == (byte) 0x38) {
            return "GIF";
        } else if (buffer[0] == (byte) 0x42 && buffer[1] == (byte) 0x4D) {
            return "BMP";
        } else if (buffer[0] == (byte) 0x49 && buffer[1] == (byte) 0x49 && buffer[2] == (byte) 0x2A && buffer[3] == (byte) 0x00) {
            return "TIFF";
        } else {
            return "Unknown";
        }
    }

    /**
     * 压缩图片(q：压缩后的质量，>1=原比例)
     * @param file 下载到的图片(如果图片是webp格式，请先转换后压缩)
     */
    public static String compress(byte[] file, double q) throws IOException {
        try (ByteArrayOutputStream boas = new ByteArrayOutputStream()) {
            ByteArrayInputStream bios = new ByteArrayInputStream(file);
            Thumbnails.of(bios).scale(q).outputQuality(1).toOutputStream(boas);
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
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        boolean jpg = ImageIO.write(image, "jpg",boas);
        if (jpg) {
            byte[] array = boas.toByteArray();
            boas.reset();
            Thumbnails.of(new ByteArrayInputStream(array))
                    .scale(q).outputQuality(1).toOutputStream(boas);
            return compress(boas.toByteArray(),q);
        }
        throw new IOException("不支持的格式");
    }
}
