package com.example.springboot.service;

import com.example.springboot.entity.AlbumPhoto;
import com.example.springboot.mapper.AlbumPhotoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                                  String location, String photoDate, String tags, String albumName) throws IOException {
        String key = cosService.generateKey(file.getOriginalFilename());
        String url = cosService.uploadFile(file, key);

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

        albumPhotoMapper.insert(photo);
        return photo;
    }

    /**
     * 获取照片列表
     */
    public List<AlbumPhoto> listPhotos(Integer status, String tag) {
        return albumPhotoMapper.selectAll(status, tag, null);
    }

    public List<AlbumPhoto> listPhotosByAlbum(Integer status, String tag, String albumName) {
        return albumPhotoMapper.selectAll(status, tag, albumName);
    }

    /**
     * 获取单张照片
     */
    public AlbumPhoto getPhoto(Long id) {
        return albumPhotoMapper.selectById(id);
    }

    /**
     * 更新照片信息
     */
    public AlbumPhoto updatePhoto(Long id, AlbumPhoto update) {
        AlbumPhoto existing = albumPhotoMapper.selectById(id);
        if (existing == null) {
            return null;
        }
        update.setPhotoId(id);
        albumPhotoMapper.update(update);
        return albumPhotoMapper.selectById(id);
    }

    /**
     * 删除照片
     */
    public boolean deletePhoto(Long id) {
        AlbumPhoto existing = albumPhotoMapper.selectById(id);
        if (existing == null) {
            return false;
        }
        // 先删 COS 文件
        String key = cosService.extractKey(existing.getUrl());
        if (key != null) {
            try {
                cosService.deleteFile(key);
            } catch (Exception e) {
                // COS 删除失败不影响数据库删除
            }
        }
        albumPhotoMapper.deleteById(id);
        return true;
    }

    /**
     * 获取所有标签
     */
    public List<String> getAllTags() {
        List<String> rawTags = albumPhotoMapper.selectTags();
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

    public List<String> getAllAlbumNames() {
        return albumPhotoMapper.selectAlbumNames();
    }
}
