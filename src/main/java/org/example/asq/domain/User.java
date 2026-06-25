package org.example.asq.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter @Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column
    private String password;

    @Column(length = 30)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Column(nullable = false, length = 20)
    private String provider = "local";

    @Column(name = "social_id", length = 100)
    private String socialId;

    @Column(name = "noti_comment", nullable = false)
    private boolean notiComment = true;

    @Column(name = "noti_like", nullable = false)
    private boolean notiLike = true;

    @Column(name = "noti_notice", nullable = false)
    private boolean notiNotice = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
