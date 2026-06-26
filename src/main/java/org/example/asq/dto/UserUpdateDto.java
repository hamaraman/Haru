package org.example.asq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserUpdateDto {
    private String name;
    private String phone;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 30, message = "닉네임은 2~30자로 입력해주세요.")
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
