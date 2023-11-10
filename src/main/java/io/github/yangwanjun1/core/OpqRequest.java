package io.github.yangwanjun1.core;


import io.github.yangwanjun1.utils.OpqUtils;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public interface OpqRequest {
    String host = "http://"+OpqWebSocket.getHost()+"/v1/LuaApiCaller?funcname=MagicCgiCmd&timeout=10&qq=";

    default <T>T sendMsg(long selfId, String body, Class<T> cls){
        Request posted = Request.post(host + selfId);
        try {
            posted.bodyString(body, ContentType.APPLICATION_JSON);
            Response response = posted.execute();
            String string = response.returnContent().asString(StandardCharsets.UTF_8);
            return OpqUtils.toBean(string,cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
