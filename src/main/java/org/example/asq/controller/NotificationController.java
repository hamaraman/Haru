package org.example.asq.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.asq.domain.User;
import org.example.asq.service.NotificationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String list(HttpSession session, Model model,
                       @RequestParam(defaultValue = "1") int page) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";
        int size = 20;
        model.addAttribute("notifications",
                notificationService.findAll(loginUser.getId(), PageRequest.of(page - 1, size)));
        model.addAttribute("currentPage", page);
        return "notification/list";
    }

    @PostMapping("/notifications/read-all")
    public String readAll(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) notificationService.markAllRead(loginUser.getId());
        return "redirect:/notifications";
    }
}
