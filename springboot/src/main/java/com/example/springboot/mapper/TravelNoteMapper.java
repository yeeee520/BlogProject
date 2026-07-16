package com.example.springboot.mapper;

import com.example.springboot.entity.TravelNote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TravelNoteMapper {

    int insert(TravelNote note);

    TravelNote selectById(@Param("noteId") Long noteId);

    List<TravelNote> selectAll(@Param("status") Integer status, @Param("tag") String tag);

    int update(TravelNote note);

    int deleteById(@Param("noteId") Long noteId);

    List<String> selectTags();

    int incrementViewCount(@Param("noteId") Long noteId);
}

