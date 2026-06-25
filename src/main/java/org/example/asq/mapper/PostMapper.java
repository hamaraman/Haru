package org.example.asq.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.asq.dto.PostDto;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    List<PostDto> searchPosts(@Param("keyword") String keyword,
                              @Param("searchType") String searchType,
                              @Param("category") String category,
                              @Param("sort") String sort,
                              @Param("offset") int offset,
                              @Param("limit") int limit);

    int countSearchPosts(@Param("keyword") String keyword,
                         @Param("searchType") String searchType,
                         @Param("category") String category);

    /** 카테고리별 글 수 (좌측 내비 배지) */
    List<Map<String, Object>> countByCategory();

    /** 대시보드용 인기/최신 게시글 (본문 요약 포함) */
    List<PostDto> findTopPosts(@Param("sort") String sort,
                               @Param("limit") int limit);
}
