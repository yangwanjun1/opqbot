package io.github.yangwanjun1.core;


import io.github.yangwanjun1.data.FileBody;
import io.github.yangwanjun1.data.ResultData;
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


    default FileBody uploadImageFile(long selfId, String body) {
        Request posted = Request.post("http://" + OpqWebSocket.getHost() + "/v1/upload?timeout=30&qq=" + selfId);
        try {
            posted.bodyString(body, ContentType.APPLICATION_JSON);
            Response response = posted.execute();
            String string = response.returnContent().asString(StandardCharsets.UTF_8);
            ResultData data = OpqUtils.toBean(string, ResultData.class);
            FileBody file = new FileBody();
            file.setWidth(3000);
            file.setHeight(3000);
            file.setFileMd5(data.getResponseData().getFileMd5());
            file.setFileSize(data.getResponseData().getFileSize());
            file.setFileId(data.getResponseData().getFileId());
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
