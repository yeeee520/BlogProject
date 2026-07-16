package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.TravelNote;
import com.example.springboot.service.TravelNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/travel-notes")
public class TravelNoteController {

    @Autowired
    private TravelNoteService travelNoteService;

    /**
     * 获取游记列表
     */
    @GetMapping
    public Result listNotes(@RequestParam(required = false) Integer status,
                           @RequestParam(required = false) String tag) {
        List<TravelNote> notes = travelNoteService.listNotes(status, tag);
        return Result.success(notes);
    }

    /**
     * 获取游记详情
     */
    @GetMapping("/{id}")
    public Result getNote(@PathVariable Long id) {
        TravelNote note = travelNoteService.getNoteAndIncrementView(id);
        if (note == null) {
            return Result.error("游记不存在");
        }
        return Result.success(note);
    }

    /**
     * 创建游记
     */
    @PostMapping
    public Result createNote(@RequestParam("title") String title,
                           @RequestParam(value = "summary", required = false) String summary,
                           @RequestParam("content") String content,
                           @RequestParam(value = "location", required = false) String location,
                           @RequestParam(value = "travelDate", required = false) String travelDate,
                           @RequestParam(value = "tags", required = false) String tags,
                           @RequestParam(value = "readTime", required = false) String readTime,
                           @RequestParam(value = "status", required = false) Integer status,
                           @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) {
        try {
            TravelNote note = new TravelNote();
            note.setTitle(title);
            note.setSummary(summary);
            note.setContent(content);
            note.setLocation(location);
            if (travelDate != null && !travelDate.isEmpty()) {
                note.setTravelDate(LocalDate.parse(travelDate));
            }
            note.setTags(tags);
            note.setReadTime(readTime);
            if (status != null) {
                note.setStatus(status);
            }
            TravelNote created = travelNoteService.createNote(note, coverFile);
            return Result.success(created);
        } catch (IOException e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 更新游记
     */
    @PutMapping("/{id}")
    public Result updateNote(@PathVariable Long id,
                           @RequestParam(value = "title", required = false) String title,
                           @RequestParam(value = "summary", required = false) String summary,
                           @RequestParam(value = "content", required = false) String content,
                           @RequestParam(value = "location", required = false) String location,
                           @RequestParam(value = "travelDate", required = false) String travelDate,
                           @RequestParam(value = "tags", required = false) String tags,
                           @RequestParam(value = "readTime", required = false) String readTime,
                           @RequestParam(value = "status", required = false) Integer status,
                           @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) {
        try {
            TravelNote update = new TravelNote();
            if (title != null) update.setTitle(title);
            if (summary != null) update.setSummary(summary);
            if (content != null) update.setContent(content);
            if (location != null) update.setLocation(location);
            if (travelDate != null && !travelDate.isEmpty()) {
                update.setTravelDate(LocalDate.parse(travelDate));
            }
            if (tags != null) update.setTags(tags);
            if (readTime != null) update.setReadTime(readTime);
            if (status != null) update.setStatus(status);
            TravelNote updated = travelNoteService.updateNote(id, update, coverFile);
            if (updated == null) {
                return Result.error("游记不存在");
            }
            return Result.success(updated);
        } catch (IOException e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除游记
     */
    @DeleteMapping("/{id}")
    public Result deleteNote(@PathVariable Long id) {
        boolean deleted = travelNoteService.deleteNote(id);
        if (!deleted) {
            return Result.error("游记不存在");
        }
        return Result.success();
    }

    /**
     * 上传游记内插图
     */
    @PostMapping("/{id}/images")
    public Result uploadImage(@PathVariable Long id,
                            @RequestParam("file") MultipartFile file) {
        try {
            String url = travelNoteService.uploadImage(file);
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有标签
     */
    @GetMapping("/tags")
    public Result getTags() {
        List<String> tags = travelNoteService.getAllTags();
        return Result.success(tags);
    }
}

