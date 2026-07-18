package com.example.springboot.mapper;

import com.example.springboot.entity.AlbumPhoto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlbumPhotoMapper {

    int insert(AlbumPhoto albumPhoto);

    AlbumPhoto selectById(@Param("photoId") Long photoId);

    List<AlbumPhoto> selectByIds(@Param("photoIds") List<Long> photoIds);

    List<AlbumPhoto> selectAllForAclSync();

    int update(AlbumPhoto albumPhoto);

    int deleteById(@Param("photoId") Long photoId);

    List<String> selectTags(@Param("publicOnly") Boolean publicOnly);

    List<AlbumPhoto> selectAll(@Param("status") Integer status,
                               @Param("tag") String tag,
                               @Param("albumName") String albumName,
                               @Param("publicOnly") Boolean publicOnly);

    List<String> selectAlbumNames(@Param("publicOnly") Boolean publicOnly);

    int batchUpdateVisibility(@Param("photoIds") List<Long> photoIds,
                              @Param("isPublic") Integer isPublic);
}
