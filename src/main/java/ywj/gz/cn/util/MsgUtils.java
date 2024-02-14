package ywj.gz.cn.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;
import ywj.gz.cn.body.pojo.AtUinLists;
import ywj.gz.cn.body.pojo.Files;
import ywj.gz.cn.body.receive.CommonlyResponseBody;
import ywj.gz.cn.body.send.CgiRequest;
import ywj.gz.cn.body.send.SendMsgBody;
import ywj.gz.cn.constants.OptionType;
import ywj.gz.cn.constants.SendType;
import ywj.gz.cn.core.Host;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Slf4j
public class MsgUtils {
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
    public static HttpRequest httpRequest(String url, RequestMethod method, String body){
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30));
        return switch (method){
            case POST -> builder.POST(HttpRequest.BodyPublishers.ofString(body)).build();
            case PUT -> builder.PUT(HttpRequest.BodyPublishers.ofString(body)).build();
            case DELETE -> builder.DELETE().build();
            default -> builder.GET().build();
        };
    }


    /**
     * 构建at体
     */
    public static List<AtUinLists> atUinLists(Long userId, String nick) {
        return userId == null ? null:atUinLists(Map.of(userId, nick));
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
     * 是否at自己
     */
    public static boolean isAtMe(List<AtUinLists> atUinLists, long selfId) {
        if (Objects.isNull(atUinLists)) {
            return false;
        }
        return atUinLists.stream().anyMatch(a -> a.getUin() == selfId);
    }

}
