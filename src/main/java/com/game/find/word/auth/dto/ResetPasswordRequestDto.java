package com.game.find.word.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDto {

    private String email;

    private String currentPassword;

    private String newPassword;

    private String confirmNewPassword;
}