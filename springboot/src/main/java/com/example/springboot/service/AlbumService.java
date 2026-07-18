package com.example.springboot.service;

import com.example.springboot.entity.AlbumPhoto;
import com.example.springboot.mapper.AlbumPhotoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class AlbumService {

    @Autowired
    private AlbumPhotoMapper albumPhotoMapper;

    @Autowired
    private CosService cosService;

    /**
     * 上传照片
     */
    public AlbumPhoto uploadPhoto(MultipartFile file, String title, String description,
                                  String location, String photoDate, String tags, String albumName,
                                  Integer isPublic) throws IOException {
        boolean publicRead = Integer.valueOf(1).equals(isPublic);
        String url = cosService.uploadImage(file, "album", publicRead);

        AlbumPhoto photo = new AlbumPhoto();
        photo.setTitle(title);
        photo.setDescription(description);
        photo.setUrl(url);
        photo.setLocation(location);
        if (photoDate != null && !photoDate.isEmpty()) {
            photo.setPhotoDate(LocalDate.parse(photoDate));
        }
        photo.setTags(tags);
        photo.setAlbumName(albumName);
        photo.setSortOrder(0);
        photo.setStatus(1);
        photo.setIsPublic(publicRead ? 1 : 0);

        try {
            albumPhotoMapper.insert(photo);
            return decoratePhoto(photo);
        } catch (RuntimeException e) {
            String key = cosService.extractKey(url);
            if (key != null) {
                try { cosService.deleteFile(key); } catch (Exception ignored) {}
            }
            throw e;
        }
    }

    /**
     * 获取照片列表
     */
    public List<AlbumPhoto> listPhotos(Integer status, String tag, boolean publicOnly) {
        return decoratePhotos(albumPhotoMapper.selectAll(resolveStatus(status, publicOnly), tag, null, publicOnly));
    }

    public List<AlbumPhoto> listPhotosByAlbum(Integer status, String tag, String albumName, boolean publicOnly) {
        return decoratePhotos(albumPhotoMapper.selectAll(resolveStatus(status, publicOnly), tag, albumName, publicOnly));
    }

    /**
     * 获取单张照片
     */
    public AlbumPhoto getPhoto(Long id, boolean publicOnly) {
        AlbumPhoto photo = albumPhotoMapper.selectById(id);
        if (photo == null || !Integer.valueOf(1).equals(photo.getStatus())) {
            return null;
        }
        if (publicOnly && !Integer.valueOf(1).equals(photo.getIsPublic())) {
            return null;
        }
        return decoratePhoto(photo);
    }

    /**
     * 更新照片信息
     */
    public AlbumPhoto updatePhoto(Long id, AlbumPhoto update) {
        AlbumPhoto existing = albumPhotoMapper.selectById(id);
        if (existing == null) {
            return null;
        }
        boolean visibilityChanged = update.getIsPublic() != null
                && !Objects.equals(existing.getIsPublic(), update.getIsPublic());
        boolean targetPublic = Integer.valueOf(1).equals(update.getIsPublic());
        if (visibilityChanged) {
            cosService.setObjectVisibility(existing.getUrl(), targetPublic);
        }
        update.setPhotoId(id);
        try {
            int updated = albumPhotoMapper.update(update);
            if (updated == 0) {
                if (visibilityChanged) {
                    cosService.setObjectVisibility(existing.getUrl(), Integer.valueOf(1).equals(existing.getIsPublic()));
                }
                return null;
            }
            return decoratePhoto(albumPhotoMapper.selectById(id));
        } catch (RuntimeException e) {
            if (visibilityChanged) {
                try {
                    cosService.setObjectVisibility(existing.getUrl(), Integer.valueOf(1).equals(existing.getIsPublic()));
                } catch (Exception ignored) {}
            }
            throw e;
        }
    }

    /**
     * 删除照片
     */
    public boolean deletePhoto(Long id) {
        AlbumPhoto existing = albumPhotoMapper.selectById(id);
        if (existing == null) {
            return false;
        }
        // 先确认 COS 对象删除成功，避免数据库已删但公开文件仍留在公网。
        String key = cosService.extractKey(existing.getUrl());
        if (key == null) {
            throw new IllegalStateException("无法识别照片的COS对象地址，已停止删除");
        }
        cosService.deleteFile(key);
        albumPhotoMapper.deleteById(id);
        return true;
    }

    /**
     * 获取所有标签
     */
    public List<String> getAllTags(boolean publicOnly) {
        List<String> rawTags = albumPhotoMapper.selectTags(publicOnly);
        Set<String> tagSet = new LinkedHashSet<>();
        for (String raw : rawTags) {
            if (raw != null && !raw.isEmpty()) {
                Arrays.stream(raw.split(","))
                        .map(String::trim)
                        .filter(t -> !t.isEmpty())
                        .forEach(tagSet::add);
            }
        }
        return List.copyOf(tagSet);
    }

    public List<String> getAllAlbumNames(boolean publicOnly) {
        return albumPhotoMapper.selectAlbumNames(publicOnly);
    }

    @Transactional
    public int batchUpdateVisibility(List<Long> photoIds, Integer isPublic) {
        List<Long> uniqueIds = photoIds.stream().filter(Objects::nonNull).distinct().toList();
        if (uniqueIds.isEmpty()) return 0;

        List<AlbumPhoto> photos = albumPhotoMapper.selectByIds(uniqueIds);
        boolean targetPublic = Integer.valueOf(1).equals(isPublic);
        List<AlbumPhoto> aclChanged = new ArrayList<>();
        try {
            for (AlbumPhoto photo : photos) {
                if (Integer.valueOf(1).equals(photo.getIsPublic()) != targetPublic) {
                    cosService.setObjectVisibility(photo.getUrl(), targetPublic);
                    aclChanged.add(photo);
                }
            }
            return albumPhotoMapper.batchUpdateVisibility(uniqueIds, isPublic);
        } catch (RuntimeException e) {
            for (AlbumPhoto photo : aclChanged) {
                try {
                    cosService.setObjectVisibility(photo.getUrl(), Integer.valueOf(1).equals(photo.getIsPublic()));
                } catch (Exception ignored) {}
            }
            throw e;
        }
    }

    public String getDownloadUrl(Long id, boolean publicOnly) {
        AlbumPhoto photo = albumPhotoMapper.selectById(id);
        if (!isVisible(photo, publicOnly)) {
            return null;
        }
        return cosService.downloadUrl(photo.getUrl(), photo.getTitle());
    }

    public int syncAllObjectAcls() {
        List<AlbumPhoto> photos = albumPhotoMapper.selectAllForAclSync();
        for (AlbumPhoto photo : photos) {
            cosService.setObjectVisibility(photo.getUrl(), Integer.valueOf(1).equals(photo.getIsPublic()));
        }
        return photos.size();
    }

    private Integer resolveStatus(Integer status, boolean publicOnly) {
        return publicOnly ? 1 : status;
    }

    private boolean isVisible(AlbumPhoto photo, boolean publicOnly) {
        if (photo == null || !Integer.valueOf(1).equals(photo.getStatus())) return false;
        return !publicOnly || Integer.valueOf(1).equals(photo.getIsPublic());
    }

    private List<AlbumPhoto> decoratePhotos(List<AlbumPhoto> photos) {
        photos.forEach(this::decoratePhoto);
        return photos;
    }

    private AlbumPhoto decoratePhoto(AlbumPhoto photo) {
        if (photo != null && photo.getUrl() != null) {
            photo.setUrl(cosService.accessibleUrl(
                    photo.getUrl(), Integer.valueOf(1).equals(photo.getIsPublic())));
        }
        return photo;
    }
}
