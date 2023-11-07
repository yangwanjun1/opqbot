package io.github.yangwanjun1.core;


import io.github.yangwanjun1.data.FileBody;
import io.github.yangwanjun1.data.ResponseData;
import io.github.yangwanjun1.data.ResultData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public interface OpqRequest {
    String host = "http://"+OpqWebSocket.getHost()+"/v1/LuaApiCaller?funcname=MagicCgiCmd&timeout=10&qq=";
    RestTemplate template =  init();
    HttpHeaders headers = load();
    private static HttpHeaders load(){
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return headers;
    }
    private static RestTemplate init(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(60000);
        factory.setConnectTimeout(60000);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }


    default <T>T sendMsg(long selfId, String body, Class<T> cls){
        HttpEntity<String> entity = new HttpEntity<>(body,headers);
        return template.postForObject(host + selfId, entity, cls);
    }


    default FileBody uploadImageFile(long selfId, HttpEntity<String> entity) {
        ResultData data = template.postForObject("http://" + OpqWebSocket.getHost() + "/v1/upload?timeout=30&qq=" + selfId, entity, ResultData.class);
        ResponseData responseData = data.getResponseData();
        if (responseData == null){
            throw new RuntimeException("上传失败");
        }
        FileBody file = new FileBody();
        file.setWidth(3000);
        file.setHeight(3000);
        file.setFileMd5(responseData.getFileMd5());
        file.setFileSize(responseData.getFileSize());
        file.setFileId(responseData.getFileId());
        return file;
    }

}
