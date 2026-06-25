package org.example.asq.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.asq.domain.Category;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String category;
    private String categoryLabel;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private String authorNickname;
    private Long userId;

    public String getCategoryLabel() {
        if (category == null) return "";
        try {
            return Category.valueOf(category).getLabel();
        } catch (IllegalArgumentException e) {
            return category;
        }
    }
}
