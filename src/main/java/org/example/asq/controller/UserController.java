package org.example.asq.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.asq.domain.Post;
import org.example.asq.domain.User;
import org.example.asq.dto.MemberStatsDto;
import org.example.asq.dto.UserDto;
import org.example.asq.dto.UserUpdateDto;
import org.example.asq.service.PostService;
import org.example.asq.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserDto userDto, RedirectAttributes attrs) {
        try {
            userService.register(userDto);
            attrs.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/user/login";
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/register";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        HttpSession session, RedirectAttributes attrs) {
        try {
            User user = userService.login(email, password);
            session.setAttribute("loginUser", user);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profileForm(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        User user = userService.findById(loginUser.getId());
        model.addAttribute("user", user);
        model.addAttribute("updateDto", new UserUpdateDto());
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute UserUpdateDto updateDto,
                                HttpSession session, RedirectAttributes attrs) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";
        try {
            User updated = userService.updateProfile(loginUser.getId(), updateDto);
            session.setAttribute("loginUser", updated);
            attrs.addFlashAttribute("message", "회원정보가 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model,
                         @RequestParam(defaultValue = "1") int page) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        User user = userService.findById(loginUser.getId());
        MemberStatsDto stats = userService.getStats(loginUser.getId());
        Page<Post> posts = postService.findByUserId(loginUser.getId(),
                PageRequest.of(page - 1, 8, Sort.by("createdAt").descending()));

        model.addAttribute("user", user);
        model.addAttribute("stats", stats);
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        return "user/mypage";
    }
}
