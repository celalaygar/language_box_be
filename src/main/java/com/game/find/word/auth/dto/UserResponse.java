package com.game.find.word.auth.dto;

import com.game.find.word.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private String token;
    private UserDto user;

    public UserResponse(String token, User user) {
        this.token = token;
        this.user = new UserDto(user);
    }


}