package com.example.springboot.service;

import com.example.springboot.entity.TravelNote;
import com.example.springboot.mapper.TravelNoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class TravelNoteService {

    @Autowired
    private TravelNoteMapper travelNoteMapper;

    @Autowired
    private CosService cosService;

    /**
     * 创建游记（可选封面图）
     */
    public TravelNote createNote(TravelNote note, MultipartFile coverFile) throws IOException {
        if (coverFile != null && !coverFile.isEmpty()) {
            String url = cosService.uploadImage(coverFile, "travel", true);
            note.setCoverUrl(url);
        }
        if (note.getUserId() == null) {
            note.setUserId(1L);
        }
        if (note.getStatus() == null) {
            note.setStatus(1);
        }
        if (note.getViewCount() == null) {
            note.setViewCount(0);
        }
        travelNoteMapper.insert(note);
        return travelNoteMapper.selectById(note.getNoteId());
    }

    /**
     * 获取游记列表
     */
    public List<TravelNote> listNotes(Integer status, String tag) {
        return travelNoteMapper.selectAll(status, tag);
    }

    /**
     * 获取游记详情
     */
    public TravelNote getNote(Long id) {
        return travelNoteMapper.selectById(id);
    }

    /**
     * 获取游记详情并增加浏览量
     */
    public TravelNote getNoteAndIncrementView(Long id) {
        travelNoteMapper.incrementViewCount(id);
        return travelNoteMapper.selectById(id);
    }

    /**
     * 更新游记
     */
    public TravelNote updateNote(Long id, TravelNote update, MultipartFile coverFile) throws IOException {
        TravelNote existing = travelNoteMapper.selectById(id);
        if (existing == null) {
            return null;
        }
        if (coverFile != null && !coverFile.isEmpty()) {
            // 删除旧封面
            if (existing.getCoverUrl() != null) {
                String oldKey = cosService.extractKey(existing.getCoverUrl());
                if (oldKey != null) {
                    try { cosService.deleteFile(oldKey); } catch (Exception ignored) {}
                }
            }
            String url = cosService.uploadImage(coverFile, "travel", true);
            update.setCoverUrl(url);
        }
        update.setNoteId(id);
        travelNoteMapper.update(update);
        return travelNoteMapper.selectById(id);
    }

    /**
     * 删除游记
     */
    public boolean deleteNote(Long id) {
        TravelNote existing = travelNoteMapper.selectById(id);
        if (existing == null) {
            return false;
        }
        // 删除封面图
        if (existing.getCoverUrl() != null) {
            String key = cosService.extractKey(existing.getCoverUrl());
            if (key != null) {
                try { cosService.deleteFile(key); } catch (Exception ignored) {}
            }
        }
        travelNoteMapper.deleteById(id);
        return true;
    }

    /**
     * 上传游记内插图
     */
    public String uploadImage(MultipartFile file) throws IOException {
        return cosService.uploadImage(file, "travel", true);
    }

    /**
     * 获取所有标签
     */
    public List<String> getAllTags() {
        List<String> rawTags = travelNoteMapper.selectTags();
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
}
