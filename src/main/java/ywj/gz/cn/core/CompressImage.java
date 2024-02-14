package ywj.gz.cn.core;

/**
 * 如果上传图片过大导致失败时，可以实现该接口完成图片压缩
 *
 */
public interface CompressImage {
    /**
     * base64的图片
     * @return 返回base64格式的压缩图片
     */
    String compressBase64(String base64);

    /**
     * 网络图片地址
     * @return 返回base64格式的压缩图片
     */
    String compressUrl(String url);

    /**
     * 本地文件路径
     * @return 返回base64格式的压缩图片
     */
    String compressFile(String filePath);


}
