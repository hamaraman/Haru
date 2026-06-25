package org.example.asq.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.asq.dto.PostDto;

import java.util.List;

@Mapper
public interface PostMapper {

    List<PostDto> searchPosts(@Param("keyword") String keyword,
                              @Param("searchType") String searchType,
                              @Param("category") String category,
                              @Param("offset") int offset,
                              @Param("limit") int limit);

    int countSearchPosts(@Param("keyword") String keyword,
                         @Param("searchType") String searchType,
                         @Param("category") String category);
}
