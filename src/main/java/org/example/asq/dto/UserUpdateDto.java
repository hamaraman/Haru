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
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirm;
}
