package org.example.asq.service;

import lombok.RequiredArgsConstructor;
import org.example.asq.domain.Category;
import org.example.asq.domain.Post;
import org.example.asq.domain.PostLike;
import org.example.asq.domain.User;
import org.example.asq.dto.PostDto;
import org.example.asq.mapper.PostMapper;
import org.example.asq.repository.PostLikeRepository;
import org.example.asq.repository.PostRepository;
import org.example.asq.repository.UserRepository;
import org.example.asq.util.HtmlSanitizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostMapper postMapper;

    private static final int PAGE_SIZE = 8;

    @Transactional
    public Post create(PostDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(HtmlSanitizer.sanitize(dto.getContent()));
        post.setCategory(dto.getCategory() != null
                ? Category.valueOf(dto.getCategory()) : Category.FREE);
        post.setUser(user);
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    @Transactional
    public void increaseViewCount(Long id) {
        postRepository.increaseViewCount(id);
    }

    @Transactional
    public Post update(Long id, PostDto dto, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        post.setTitle(dto.getTitle());
        post.setContent(HtmlSanitizer.sanitize(dto.getContent()));
        if (dto.getCategory() != null) {
            post.setCategory(Category.valueOf(dto.getCategory()));
        }
        return postRepository.save(post);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        postRepository.delete(post);
    }

    @Transactional
    public Map<String, Object> toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        boolean liked;
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            postLikeRepository.findByPostIdAndUserId(postId, userId)
                    .ifPresent(postLikeRepository::delete);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            liked = false;
        } else {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            postLikeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            liked = true;
        }
        postRepository.save(post);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", post.getLikeCount());
        return result;
    }

    public boolean isLikedByUser(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    public Map<String, Object> searchPosts(String keyword, String searchType,
                                           String category, String sort, int page) {
        int offset = (page - 1) * PAGE_SIZE;
        List<PostDto> posts = postMapper.searchPosts(keyword, searchType, category, sort, offset, PAGE_SIZE);
        int total = postMapper.countSearchPosts(keyword, searchType, category);
        int totalPages = total == 0 ? 1 : (int) Math.ceil((double) total / PAGE_SIZE);

        Map<String, Object> result = new HashMap<>();
        result.put("posts", posts);
        result.put("total", total);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);
        return result;
    }

    /** 좌측 내비 카테고리 배지용: 카테고리명 → 글 수 */
    public Map<String, Integer> categoryCounts() {
        Map<String, Integer> counts = new HashMap<>();
        int total = 0;
        for (Map<String, Object> row : postMapper.countByCategory()) {
            String cat = String.valueOf(row.get("category"));
            int cnt = ((Number) row.get("cnt")).intValue();
            counts.put(cat, cnt);
            total += cnt;
        }
        counts.put("ALL", total);
        return counts;
    }

    /** 대시보드: 인기/최신 게시글 */
    public List<PostDto> topPosts(String sort, int limit) {
        return postMapper.findTopPosts(sort, limit);
    }

    @Transactional(readOnly = true)
    public Page<Post> findByUserId(Long userId, Pageable pageable) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
