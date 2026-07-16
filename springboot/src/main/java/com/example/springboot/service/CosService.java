package com.example.springboot.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class CosService {

    @Autowired
    private COSClient cosClient;

    @Autowired
    private String cosBucketName;

    /**
     * 上传文件到 COS
     * @param file 文件
     * @param key  COS 对象 key
     * @return 文件访问 URL
     */
    public String uploadFile(MultipartFile file, String key) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        metadata.setCacheControl("max-age=31536000");

        PutObjectRequest putRequest = new PutObjectRequest(cosBucketName, key, file.getInputStream(), metadata);
        cosClient.putObject(putRequest);

        return "https://" + cosBucketName + ".cos." + cosClient.getClientConfig().getRegion().getRegionName() + ".myqcloud.com/" + key;
    }

    /**
     * 删除 COS 文件
     * @param key COS 对象 key
     */
    public void deleteFile(String key) {
        cosClient.deleteObject(cosBucketName, key);
    }

    /**
     * 从 URL 中提取 key
     * @param url COS 文件 URL
     * @return 对象 key
     */
    public String extractKey(String url) {
        if (url == null || url.isEmpty()) return null;
        // URL 格式: https://bucket.cos.region.myqcloud.com/album/uuid.jpg
        int idx = url.indexOf(".myqcloud.com/");
        if (idx == -1) return null;
        return url.substring(idx + ".myqcloud.com/".length());
    }

    /**
     * 生成唯一的对象 key
     * @param originalFilename 原始文件名
     * @return 对象 key
     */
    public String generateKey(String originalFilename) {
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "album/" + UUID.randomUUID().toString().replace("-", "") + ext;
    }
}
