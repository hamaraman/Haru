package org.example.asq.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserUpdateDto {
    private String name;
    private String phone;
    private String nickname;
    private String email;
    private String profileImage;
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirm;

    // 알림 설정
    private boolean notiComment;
    private boolean notiLike;
    private boolean notiNotice;
}
