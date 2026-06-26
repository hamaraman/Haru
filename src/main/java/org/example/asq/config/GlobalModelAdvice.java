package org.example.asq.config;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.asq.domain.Category;
import org.example.asq.domain.User;
import org.example.asq.service.NotificationService;
import org.example.asq.service.PostService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final PostService postService;
    private final NotificationService notificationService;

    @ModelAttribute("categories")
    public Category[] categories() {
        return Category.values();
    }

    @ModelAttribute("catCounts")
    public Map<String, Integer> catCounts() {
        return postService.categoryCounts();
    }

    @ModelAttribute("unreadCount")
    public long unreadCount(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return 0;
        return notificationService.countUnread(loginUser.getId());
    }
}
