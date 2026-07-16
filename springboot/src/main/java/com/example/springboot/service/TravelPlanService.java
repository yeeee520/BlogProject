package com.example.springboot.service;

import com.example.springboot.entity.TravelPlan;
import com.example.springboot.mapper.TravelPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class TravelPlanService {

    @Autowired
    private TravelPlanMapper travelPlanMapper;

    @Autowired
    private CosService cosService;

    /**
     * 创建计划
     */
    public TravelPlan createPlan(TravelPlan plan, MultipartFile coverFile) throws IOException {
        if (coverFile != null && !coverFile.isEmpty()) {
            String key = cosService.generateKey(coverFile.getOriginalFilename());
            String url = cosService.uploadFile(coverFile, key);
            plan.setCoverUrl(url);
        }
        if (plan.getStatus() == null) {
            plan.setStatus("planning");
        }
        if (plan.getSortOrder() == null) {
            plan.setSortOrder(0);
        }
        travelPlanMapper.insert(plan);
        return travelPlanMapper.selectById(plan.getPlanId());
    }

    /**
     * 获取计划列表
     */
    public List<TravelPlan> listPlans(String status) {
        return travelPlanMapper.selectAll(status);
    }

    /**
     * 获取计划详情
     */
    public TravelPlan getPlan(Long id) {
        return travelPlanMapper.selectById(id);
    }

    /**
     * 更新计划
     */
    public TravelPlan updatePlan(Long id, TravelPlan update, MultipartFile coverFile) throws IOException {
        TravelPlan existing = travelPlanMapper.selectById(id);
        if (existing == null) {
            return null;
        }
        if (coverFile != null && !coverFile.isEmpty()) {
            if (existing.getCoverUrl() != null) {
                String oldKey = cosService.extractKey(existing.getCoverUrl());
                if (oldKey != null) {
                    try { cosService.deleteFile(oldKey); } catch (Exception ignored) {}
                }
            }
            String key = cosService.generateKey(coverFile.getOriginalFilename());
            String url = cosService.uploadFile(coverFile, key);
            update.setCoverUrl(url);
        }
        update.setPlanId(id);
        travelPlanMapper.update(update);
        return travelPlanMapper.selectById(id);
    }

    /**
     * 删除计划
     */
    public boolean deletePlan(Long id) {
        TravelPlan existing = travelPlanMapper.selectById(id);
        if (existing == null) {
            return false;
        }
        if (existing.getCoverUrl() != null) {
            String key = cosService.extractKey(existing.getCoverUrl());
            if (key != null) {
                try { cosService.deleteFile(key); } catch (Exception ignored) {}
            }
        }
        travelPlanMapper.deleteById(id);
        return true;
    }
}

