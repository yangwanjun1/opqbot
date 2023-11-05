package opq.bot.frame.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;
import opq.bot.frame.core.OpqRequest;
import opq.bot.frame.data.*;
import org.springframework.http.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    public static HttpEntity<String> uploadImageFileBody(String url, int type, boolean toBase) {
        FileData fileData = new FileData();
        fileData.setCgiRequest(new CgiRequest());
        fileData.getCgiRequest().setCommandId(type);
        if (toBase) {
            fileData.getCgiRequest().setBase64Buf(compress(url));
        } else {
            fileData.getCgiRequest().setFileUrl(url);
        }
        return new HttpEntity<>(toJsonString(fileData));
    }

    public static HttpEntity<String> uploadImageFileBody(File file, int type) {
        FileData fileData = new FileData();
        fileData.setCgiRequest(new CgiRequest());
        fileData.getCgiRequest().setCommandId(type);
        fileData.getCgiRequest().setBase64Buf(compress(file));
        String string = toJsonString(fileData);
        return new HttpEntity<>(string);
    }

    /**
     * 压缩图片
     *
     * @param url 网络地址
     */
    public static String compress(String url) {
        try (ByteArrayOutputStream boas = new ByteArrayOutputStream()) {
            ResponseEntity<byte[]> entity = OpqRequest.template.exchange(url, HttpMethod.GET, null, byte[].class);
            HttpStatusCode code = entity.getStatusCode();
            byte[] body = Objects.requireNonNull(entity.getBody());
            if (code != HttpStatus.OK) {
                throw new RuntimeException(new String(body));
            }
            ByteArrayInputStream bois = new ByteArrayInputStream(body);
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

}