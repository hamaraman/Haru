package org.example.asq.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.asq.domain.User;
import org.example.asq.repository.CommentRepository;
import org.example.asq.repository.PostRepository;
import org.example.asq.repository.UserRepository;
import org.example.asq.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    private boolean isAdmin(HttpSession session) {
        User u = (User) session.getAttribute("loginUser");
        return u != null && u.isAdmin();
    }

    @GetMapping("")
    public String index(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        model.addAttribute("userCount",  userRepository.count());
        model.addAttribute("postCount",  postRepository.count());
        model.addAttribute("commentCount", commentRepository.count());
        return "admin/index";
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model,
                        @RequestParam(defaultValue = "1") int page) {
        if (!isAdmin(session)) return "redirect:/";
        int size = 20;
        model.addAttribute("users", userRepository.findAll(
                PageRequest.of(page - 1, size, Sort.by("id").ascending())));
        model.addAttribute("currentPage", page);
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session, RedirectAttributes attrs) {
        if (!isAdmin(session)) return "redirect:/";
        try {
            userService.deleteById(id);
            attrs.addFlashAttribute("message", "회원이 삭제되었습니다.");
        } catch (Exception e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
