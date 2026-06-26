package org.example.asq.service;

import lombok.RequiredArgsConstructor;
import org.example.asq.domain.Comment;
import org.example.asq.domain.Post;
import org.example.asq.domain.User;
import org.example.asq.repository.CommentRepository;
import org.example.asq.repository.PostRepository;
import org.example.asq.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public Comment create(Long postId, String content, Long userId, Long parentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);

        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId).orElse(null);
            if (parent != null) {
                comment.setParent(parent);
                // notify parent comment's author
                if (!parent.getUser().getId().equals(userId)) {
                    notificationService.notify(parent.getUser(), "REPLY",
                            user.getNickname() + "님이 내 댓글에 답글을 남겼습니다.", postId);
                }
            }
        } else {
            // notify post owner about new comment
            if (!post.getUser().getId().equals(userId)) {
                String title = post.getTitle().length() > 20
                        ? post.getTitle().substring(0, 20) + "…" : post.getTitle();
                notificationService.notify(post.getUser(), "COMMENT",
                        user.getNickname() + "님이 '" + title + "'에 댓글을 남겼습니다.", postId);
            }
        }

        return commentRepository.save(comment);
    }

    @Transactional
    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> findByPostId(Long postId) {
        List<Comment> all = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        Map<Long, Comment> map = new LinkedHashMap<>();
        List<Comment> topLevel = new ArrayList<>();
        for (Comment c : all) {
            map.put(c.getId(), c);
        }
        for (Comment c : all) {
            if (c.getParentId() == null) {
                topLevel.add(c);
            } else {
                Comment parent = map.get(c.getParentId());
                if (parent != null) parent.getReplies().add(c);
            }
        }
        return topLevel;
    }
}
