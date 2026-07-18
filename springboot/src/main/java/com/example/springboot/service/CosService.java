package com.example.springboot.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.ResponseHeaderOverrides;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Service
public class CosService {

    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;

    @Autowired
    private COSClient cosClient;

    @Autowired
    private String cosBucketName;

    @Value("${cos.private-url-minutes:10}")
    private long privateUrlMinutes;

    public String uploadImage(MultipartFile file, String prefix, boolean publicRead) throws IOException {
        ImageType imageType = validateImage(file);
        String safePrefix = normalizePrefix(prefix);
        String key = safePrefix + UUID.randomUUID().toString().replace("-", "") + imageType.extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(imageType.contentType);
        metadata.setContentDisposition("inline");
        metadata.setCacheControl(publicRead ? "public,max-age=31536000" : "private,no-store");

        try (InputStream input = file.getInputStream()) {
            PutObjectRequest putRequest = new PutObjectRequest(cosBucketName, key, input, metadata)
                    .withCannedAcl(publicRead
                            ? CannedAccessControlList.PublicRead
                            : CannedAccessControlList.Private);
            cosClient.putObject(putRequest);
        }
        return canonicalUrl(key);
    }

    public void setObjectVisibility(String storedUrl, boolean publicRead) {
        String key = requireKey(storedUrl);
        cosClient.setObjectAcl(cosBucketName, key, publicRead
                ? CannedAccessControlList.PublicRead
                : CannedAccessControlList.Private);
    }

    public String accessibleUrl(String storedUrl, boolean publicRead) {
        if (publicRead) {
            return storedUrl;
        }
        String key = requireKey(storedUrl);
        Date expiration = Date.from(java.time.Instant.now().plus(Duration.ofMinutes(privateUrlMinutes)));
        return cosClient.generatePresignedUrl(cosBucketName, key, expiration).toString();
    }

    public String downloadUrl(String storedUrl, String filename) {
        String key = requireKey(storedUrl);
        Date expiration = Date.from(java.time.Instant.now().plus(Duration.ofMinutes(privateUrlMinutes)));
        String safeFilename = sanitizeFilename(filename, key);

        ResponseHeaderOverrides headers = new ResponseHeaderOverrides();
        headers.setContentDisposition("attachment; filename*=UTF-8''" + encodeFilename(safeFilename));
        headers.setCacheControl("private,no-store");

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                cosBucketName, key, HttpMethodName.GET);
        request.setExpiration(expiration);
        request.setResponseHeaders(headers);
        return cosClient.generatePresignedUrl(request).toString();
    }

    public void deleteFile(String key) {
        cosClient.deleteObject(cosBucketName, key);
    }

    public String extractKey(String url) {
        if (url == null || url.isBlank()) return null;
        int idx = url.indexOf(".myqcloud.com/");
        if (idx == -1) return null;
        String key = url.substring(idx + ".myqcloud.com/".length());
        int queryIndex = key.indexOf('?');
        return queryIndex >= 0 ? key.substring(0, queryIndex) : key;
    }

    private ImageType validateImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("图片文件不能为空");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IOException("单张图片不能超过10MB");
        }

        byte[] header;
        try (InputStream input = file.getInputStream()) {
            header = input.readNBytes(12);
        }
        ImageType type = ImageType.detect(header);
        if (type == null) {
            throw new IOException("仅支持真实的 JPEG、PNG、GIF 或 WebP 图片");
        }
        return type;
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null || !prefix.matches("[a-z0-9/_-]+")) {
            throw new IllegalArgumentException("COS对象目录不合法");
        }
        return prefix.endsWith("/") ? prefix : prefix + "/";
    }

    private String canonicalUrl(String key) {
        return "https://" + cosBucketName + ".cos."
                + cosClient.getClientConfig().getRegion().getRegionName()
                + ".myqcloud.com/" + key;
    }

    private String requireKey(String storedUrl) {
        String key = extractKey(storedUrl);
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("无法识别照片的COS对象地址");
        }
        return key;
    }

    private String sanitizeFilename(String filename, String key) {
        String cleaned = filename == null ? "" : filename
                .replaceAll("[\\r\\n\\\\/:*?\"<>|]", "_")
                .trim();
        String extension = key.contains(".") ? key.substring(key.lastIndexOf('.')) : ".jpg";
        if (cleaned.isEmpty()) cleaned = "photo";
        if (!cleaned.toLowerCase(Locale.ROOT).endsWith(extension)) cleaned += extension;
        return cleaned;
    }

    private String encodeFilename(String filename) {
        StringBuilder encoded = new StringBuilder();
        for (byte value : filename.getBytes(StandardCharsets.UTF_8)) {
            int unsigned = value & 0xff;
            if ((unsigned >= 'a' && unsigned <= 'z') || (unsigned >= 'A' && unsigned <= 'Z')
                    || (unsigned >= '0' && unsigned <= '9') || unsigned == '-' || unsigned == '_'
                    || unsigned == '.') {
                encoded.append((char) unsigned);
            } else {
                encoded.append('%').append(String.format("%02X", unsigned));
            }
        }
        return encoded.toString();
    }

    private enum ImageType {
        JPEG(".jpg", "image/jpeg"),
        PNG(".png", "image/png"),
        GIF(".gif", "image/gif"),
        WEBP(".webp", "image/webp");

        private final String extension;
        private final String contentType;

        ImageType(String extension, String contentType) {
            this.extension = extension;
            this.contentType = contentType;
        }

        private static ImageType detect(byte[] bytes) {
            if (bytes.length >= 3 && (bytes[0] & 0xff) == 0xff
                    && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff) return JPEG;
            if (bytes.length >= 8 && (bytes[0] & 0xff) == 0x89 && bytes[1] == 0x50
                    && bytes[2] == 0x4e && bytes[3] == 0x47 && bytes[4] == 0x0d
                    && bytes[5] == 0x0a && bytes[6] == 0x1a && bytes[7] == 0x0a) return PNG;
            if (bytes.length >= 6) {
                String signature = new String(bytes, 0, 6, StandardCharsets.US_ASCII);
                if ("GIF87a".equals(signature) || "GIF89a".equals(signature)) return GIF;
            }
            if (bytes.length >= 12
                    && "RIFF".equals(new String(bytes, 0, 4, StandardCharsets.US_ASCII))
                    && "WEBP".equals(new String(bytes, 8, 4, StandardCharsets.US_ASCII))) return WEBP;
            return null;
        }
    }
}
