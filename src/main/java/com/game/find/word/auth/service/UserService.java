package com.game.find.word.auth.service;

import com.game.find.word.auth.dto.RegisterRequest;
import com.game.find.word.auth.dto.ResetPasswordRequestDto;
import com.game.find.word.auth.dto.UserDto;
import com.game.find.word.auth.dto.UserUpdateDto;
import com.game.find.word.auth.model.User;
import com.game.find.word.base.model.BaseResponse;

import java.util.List;

public interface UserService {
    User register(RegisterRequest request);
    List<UserDto> findAll();
    User findById(Long id);
    User findUserByAuthentication();
    BaseResponse<UserDto> updateMe(UserUpdateDto request);
    BaseResponse<Boolean> changePassword(ResetPasswordRequestDto request);
    BaseResponse<UserDto> getMe();

}