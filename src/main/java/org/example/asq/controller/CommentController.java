package org.example.asq.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.asq.domain.User;
import org.example.asq.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}/comment")
    public String create(@PathVariable Long postId, @RequestParam String content,
                         HttpSession session, RedirectAttributes attrs) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";
        try {
            commentService.create(postId, content, loginUser.getId());
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/post/" + postId;
    }

    @PostMapping("/comment/{commentId}/delete")
    public String delete(@PathVariable Long commentId, @RequestParam Long postId,
                         HttpSession session, RedirectAttributes attrs) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";
        try {
            commentService.delete(commentId, loginUser.getId());
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/post/" + postId;
    }
}
