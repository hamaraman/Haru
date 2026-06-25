package org.example.asq.repository;

import org.example.asq.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findBySocialId(String socialId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    @Query("SELECT COALESCE(SUM(p.likeCount), 0) FROM Post p WHERE p.user.id = :userId")
    long sumLikeCountByUserId(Long userId);
}
