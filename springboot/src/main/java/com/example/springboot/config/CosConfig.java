package com.example.springboot.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosConfig {

    @Value("${cos.secret-id}")
    private String secretId;

    @Value("${cos.secret-key}")
    private String secretKey;

    @Value("${cos.region}")
    private String region;

    @Bean(destroyMethod = "shutdown")
    public COSClient cosClient() {
        requireConfigured("COS_SECRET_ID", secretId);
        requireConfigured("COS_SECRET_KEY", secretKey);
        requireConfigured("COS_REGION", region);
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        return new COSClient(cred, clientConfig);
    }

    @Value("${cos.bucket-name}")
    private String bucketName;

    @Bean
    public String cosBucketName() {
        requireConfigured("COS_BUCKET_NAME", bucketName);
        return bucketName;
    }

    private void requireConfigured(String name, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " 未配置");
        }
    }
}
