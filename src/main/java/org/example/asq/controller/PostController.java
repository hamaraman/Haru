package org.example.asq.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.asq.domain.Category;
import org.example.asq.domain.Comment;
import org.example.asq.domain.Post;
import org.example.asq.domain.User;
import org.example.asq.dto.PostDto;
import org.example.asq.service.CommentService;
import org.example.asq.service.PostService;
import org.example.asq.util.HtmlSanitizer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    /** 홈 대시보드: 히어로 + 인기 게시글 + 최신 게시글 + 우측 위젯 */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("popularPosts", postService.topPosts("popular", 3));
        model.addAttribute("latestPosts", postService.topPosts("latest", 6));
        return "index";
    }

    /** 게시글 목록: 검색 + 정렬(최신/인기/조회) + 카테고리 필터 + 페이지네이션 */
    @GetMapping("/posts")
    public String list(Model model,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "all") String searchType,
                       @RequestParam(defaultValue = "") String category,
                       @RequestParam(defaultValue = "latest") String sort,
                       @RequestParam(defaultValue = "1") int page) {
        Map<String, Object> result = postService.searchPosts(keyword, searchType, category, sort, page);
        model.addAttribute("posts", result.get("posts"));
        model.addAttribute("totalPages", result.get("totalPages"));
        model.addAttribute("currentPage", result.get("currentPage"));
        model.addAttribute("total", result.get("total"));
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("category", category);
        model.addAttribute("categoryLabel",
                (category == null || category.isEmpty()) ? null : Category.labelOf(category));
        model.addAttribute("sort", sort);
        return "post/list";
    }

    @GetMapping("/post/create")
    public String createForm(HttpSession session, Model model) {
        if (session.getAttribute("loginUser") == null) return "redirect:/user/login";
        model.addAttribute("postDto", new PostDto());
        return "post/form";
    }

    @PostMapping("/post/create")
    public String create(@ModelAttribute PostDto postDto, HttpSession session,
                         RedirectAttributes attrs) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";
        try {
            Post post = postService.create(postDto, loginUser.getId());
            return "redirect:/post/" + post.getId();
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/post/create";
        }
    }

    @GetMapping("/post/{id}")
    public String view(@PathVariable Long id, HttpSession session, Model model) {
        Post post = postService.findById(id);
        List<Comment> comments = commentService.findByPostId(id);
        User loginUser = (User) session.getAttribute("loginUser");
        boolean liked = loginUser != null && postService.isLikedByUser(id, loginUser.getId());

        model.addAttribute("post", post);
        model.addAttribute("safeContent", HtmlSanitizer.sanitize(post.getContent()));
        model.addAttribute("comments", comments);
        model.addAttribute("liked", liked);
        return "post/view";
    }

    @GetMapping("/post/{id}/edit")
    public String editForm(@PathVariable Long id, HttpSession session, Model model,
                           RedirectAttributes attrs) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        Post post = postService.findByIdForEdit(id);
        if (!post.getUser().getId().equals(loginUser.getId())) {
            attrs.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/post/" + id;
        }
        PostDto dto = new PostDto();
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCategory(post.getCategory().name());
        model.addAttribute("postDto", dto);
        model.addAttribute("postId", id);
        model.addAttribute("categories", Category.values());
        return "post/form";
    }

    @PostMapping("/post/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute PostDto postDto,
                       HttpSession session, RedirectAttributes attrs) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";
        try {
            postService.update(id, postDto, loginUser.getId());
            return "redirect:/post/" + id;
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/post/" + id + "/edit";
        }
    }

    @PostMapping("/post/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, RedirectAttributes attrs) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";
        try {
            postService.delete(id, loginUser.getId());
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/post/" + id;
        }
        return "redirect:/";
    }

    @PostMapping("/post/{id}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> like(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }
        Map<String, Object> result = postService.toggleLike(id, loginUser.getId());
        return ResponseEntity.ok(result);
    }
}
