package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.TravelPlan;
import com.example.springboot.service.TravelPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/travel-plans")
public class TravelPlanController {

    @Autowired
    private TravelPlanService travelPlanService;

    /**
     * 获取计划列表
     */
    @GetMapping
    public Result listPlans(@RequestParam(required = false) String status) {
        List<TravelPlan> plans = travelPlanService.listPlans(status);
        return Result.success(plans);
    }

    /**
     * 获取计划详情
     */
    @GetMapping("/{id}")
    public Result getPlan(@PathVariable Long id) {
        TravelPlan plan = travelPlanService.getPlan(id);
        if (plan == null) {
            return Result.error("计划不存在");
        }
        return Result.success(plan);
    }

    /**
     * 创建计划
     */
    @PostMapping
    public Result createPlan(@RequestParam("title") String title,
                           @RequestParam(value = "description", required = false) String description,
                           @RequestParam(value = "planDate", required = false) String planDate,
                           @RequestParam(value = "status", required = false) String status,
                           @RequestParam(value = "sortOrder", required = false) Integer sortOrder,
                           @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) {
        try {
            TravelPlan plan = new TravelPlan();
            plan.setTitle(title);
            plan.setDescription(description);
            plan.setPlanDate(planDate);
            if (status != null) plan.setStatus(status);
            if (sortOrder != null) plan.setSortOrder(sortOrder);
            TravelPlan created = travelPlanService.createPlan(plan, coverFile);
            return Result.success(created);
        } catch (IOException e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 更新计划
     */
    @PutMapping("/{id}")
    public Result updatePlan(@PathVariable Long id,
                           @RequestParam(value = "title", required = false) String title,
                           @RequestParam(value = "description", required = false) String description,
                           @RequestParam(value = "planDate", required = false) String planDate,
                           @RequestParam(value = "status", required = false) String status,
                           @RequestParam(value = "sortOrder", required = false) Integer sortOrder,
                           @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) {
        try {
            TravelPlan update = new TravelPlan();
            if (title != null) update.setTitle(title);
            if (description != null) update.setDescription(description);
            if (planDate != null) update.setPlanDate(planDate);
            if (status != null) update.setStatus(status);
            if (sortOrder != null) update.setSortOrder(sortOrder);
            TravelPlan updated = travelPlanService.updatePlan(id, update, coverFile);
            if (updated == null) {
                return Result.error("计划不存在");
            }
            return Result.success(updated);
        } catch (IOException e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除计划
     */
    @DeleteMapping("/{id}")
    public Result deletePlan(@PathVariable Long id) {
        boolean deleted = travelPlanService.deletePlan(id);
        if (!deleted) {
            return Result.error("计划不存在");
        }
        return Result.success();
    }
}

