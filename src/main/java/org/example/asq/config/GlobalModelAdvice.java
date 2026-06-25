package org.example.asq.config;

import lombok.RequiredArgsConstructor;
import org.example.asq.domain.Category;
import org.example.asq.service.PostService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

/**
 * 모든 화면 공통 셸(좌측 내비)에서 사용하는 데이터를 주입한다.
 * - categories: 카테고리 enum 목록
 * - catCounts : 카테고리별 글 수 배지
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final PostService postService;

    @ModelAttribute("categories")
    public Category[] categories() {
        return Category.values();
    }

    @ModelAttribute("catCounts")
    public Map<String, Integer> catCounts() {
        return postService.categoryCounts();
    }
}
