package org.example.asq.service;

import lombok.RequiredArgsConstructor;
import org.example.asq.domain.Bookmark;
import org.example.asq.domain.Post;
import org.example.asq.domain.User;
import org.example.asq.repository.BookmarkRepository;
import org.example.asq.repository.PostRepository;
import org.example.asq.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggle(Long postId, Long userId) {
        if (bookmarkRepository.existsByPostIdAndUserId(postId, userId)) {
            bookmarkRepository.findByPostIdAndUserId(postId, userId)
                    .ifPresent(bookmarkRepository::delete);
            return false;
        }
        Post post = postRepository.findById(postId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        Bookmark bm = new Bookmark();
        bm.setPost(post);
        bm.setUser(user);
        bookmarkRepository.save(bm);
        return true;
    }

    public boolean isBookmarked(Long postId, Long userId) {
        return bookmarkRepository.existsByPostIdAndUserId(postId, userId);
    }

    @Transactional(readOnly = true)
    public Page<Bookmark> findByUserId(Long userId, Pageable pageable) {
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
