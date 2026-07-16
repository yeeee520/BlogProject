package com.example.springboot.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TravelNote {
    private Long noteId;
    private Long userId;
    private String title;
    private String summary;
    private String content;
    private String coverUrl;
    private String images;
    private String location;
    private LocalDate travelDate;
    private String tags;
    private String readTime;
    private Integer viewCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getNoteId() { return noteId; }
    public void setNoteId(Long noteId) { this.noteId = noteId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getReadTime() { return readTime; }
    public void setReadTime(String readTime) { this.readTime = readTime; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}

