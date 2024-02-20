package ywj.gz.cn.core;

import ywj.gz.cn.body.pojo.Files;

/**
 * 缓存上传之后的图片
 */
public interface CacheImage {
    /**
     * 存放上传成功的image
     * @param image 图片数据（上传成功时返回的图片数据）
     * (二选一，base64时，不走该方法)
     * @param path 本地图片的路径
     * @param url 网络图片地址
     *  base上传时，倒数两个参数为空
     */
    void cache(Files image, String path, String url);

    /**
     * 获取缓存的图片数据
     * @param key 通过key值获取图片
     * @return 图片数据
     */
    Files getImage(String key);
}
