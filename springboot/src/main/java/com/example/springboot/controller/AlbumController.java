package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.AlbumPhoto;
import com.example.springboot.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/album")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    /**
     * 获取照片列表
     * @param status 状态过滤（可选）
     * @param tag 标签过滤（可选）
     */
    @GetMapping("/photos")
    public Result listPhotos(@RequestParam(required = false) Integer status,
                            @RequestParam(required = false) String tag,
                            @RequestParam(required = false) String albumName) {
        List<AlbumPhoto> photos;
        if (albumName != null) {
            photos = albumService.listPhotosByAlbum(status, tag, albumName);
        } else {
            photos = albumService.listPhotos(status, tag);
        }
        return Result.success(photos);
    }

    /**
     * 获取单张照片详情
     */
    @GetMapping("/photos/{id}")
    public Result getPhoto(@PathVariable Long id) {
        AlbumPhoto photo = albumService.getPhoto(id);
        if (photo == null) {
            return Result.error("照片不存在");
        }
        return Result.success(photo);
    }

    /**
     * 上传照片
     * @param file 图片文件
     * @param title 标题
     * @param description 描述
     * @param location 地点
     * @param photoDate 拍摄日期
     * @param tags 标签（逗号分隔）
     */
    @PostMapping("/photos")
    public Result uploadPhoto(@RequestParam("file") MultipartFile file,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) String location,
                            @RequestParam(required = false) String photoDate,
                            @RequestParam(required = false) String tags,
                            @RequestParam(required = false) String albumName) {
        try {
            AlbumPhoto photo = albumService.uploadPhoto(file, title, description, location, photoDate, tags, albumName);
            return Result.success(photo);
        } catch (IOException e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 更新照片信息
     */
    @PutMapping("/photos/{id}")
    public Result updatePhoto(@PathVariable Long id, @RequestBody AlbumPhoto photo) {
        AlbumPhoto updated = albumService.updatePhoto(id, photo);
        if (updated == null) {
            return Result.error("照片不存在");
        }
        return Result.success(updated);
    }

    /**
     * 删除照片
     */
    @DeleteMapping("/photos/{id}")
    public Result deletePhoto(@PathVariable Long id) {
        boolean deleted = albumService.deletePhoto(id);
        if (!deleted) {
            return Result.error("照片不存在");
        }
        return Result.success();
    }

    /**
     * 获取所有标签列表
     */
    @GetMapping("/tags")
    public Result getTags() {
        List<String> tags = albumService.getAllTags();
        return Result.success(tags);
    }

    @GetMapping("/albums")
    public Result getAlbumNames() {
        List<String> names = albumService.getAllAlbumNames();
        return Result.success(names);
    }

    /**
     * 批量上传照片
     * @param files 图片文件列表
     * @param tags 公共标签（可选）
     * @param location 公共拍摄地点（可选）
     * @param photoDate 公共拍摄日期（可选）
     */
    @PostMapping("/photos/batch")
    public Result batchUpload(@RequestParam("files") MultipartFile[] files,
                              @RequestParam(required = false) String tags,
                              @RequestParam(required = false) String location,
                              @RequestParam(required = false) String photoDate,
                              @RequestParam(required = false) String albumName) {
        if (files == null || files.length == 0) {
            return Result.error("请选择至少一张图片");
        }
        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        for (MultipartFile file : files) {
            Map<String, Object> item = new HashMap<>();
            item.put("filename", file.getOriginalFilename());
            try {
                // 使用文件名（不含扩展名）作为默认标题
                String filename = file.getOriginalFilename();
                String title = "";
                if (filename != null) {
                    int dotIdx = filename.lastIndexOf('.');
                    title = dotIdx > 0 ? filename.substring(0, dotIdx) : filename;
                }
                AlbumPhoto photo = albumService.uploadPhoto(file, title, null, location, photoDate, tags, null);
                item.put("success", true);
                item.put("photo", photo);
                successCount++;
            } catch (Exception e) {
                item.put("success", false);
                item.put("error", e.getMessage());
                failCount++;
            }
            results.add(item);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("results", results);
        data.put("total", files.length);
        data.put("successCount", successCount);
        data.put("failCount", failCount);
        return Result.success(data);
    }
}
