package org.example.asq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.asq.domain.Category;
import org.jsoup.Jsoup;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String category;
    private String categoryLabel;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 200, message = "제목은 200자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
    private String profileImage;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private String authorNickname;
    private Long userId;

    public String getCategoryLabel() {
        if (category == null) return "";
        return Category.labelOf(category);
    }

    public String getCategoryIcon() {
        if (category == null) return "•";
        return Category.iconOf(category);
    }

    /** 대시보드 카드용 본문 요약(HTML 태그 제거 후 잘라내기) */
    public String getSummary() {
        if (content == null || content.isBlank()) return "";
        String text = Jsoup.parse(content).text().trim();
        return text.length() > 90 ? text.substring(0, 90) + "…" : text;
    }
}
