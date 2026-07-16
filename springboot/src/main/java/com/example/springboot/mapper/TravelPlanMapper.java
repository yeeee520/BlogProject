package com.example.springboot.mapper;

import com.example.springboot.entity.TravelPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TravelPlanMapper {

    int insert(TravelPlan plan);

    TravelPlan selectById(@Param("planId") Long planId);

    List<TravelPlan> selectAll(@Param("status") String status);

    int update(TravelPlan plan);

    int deleteById(@Param("planId") Long planId);
}

