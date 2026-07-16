package com.example.springboot.mapper;

import com.example.springboot.entity.AlbumPhoto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlbumPhotoMapper {

    int insert(AlbumPhoto albumPhoto);

    AlbumPhoto selectById(@Param("photoId") Long photoId);

    List<AlbumPhoto> selectAll(@Param("status") Integer status, @Param("tag") String tag);

    int update(AlbumPhoto albumPhoto);

    int deleteById(@Param("photoId") Long photoId);

    List<String> selectTags();

    List<AlbumPhoto> selectAll(@Param("status") Integer status, @Param("tag") String tag, @Param("albumName") String albumName);

    List<String> selectAlbumNames();
}