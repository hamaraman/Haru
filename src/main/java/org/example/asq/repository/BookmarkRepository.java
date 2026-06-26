package org.example.asq.repository;

import org.example.asq.domain.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    Optional<Bookmark> findByPostIdAndUserId(Long postId, Long userId);
    Page<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
