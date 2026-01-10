package com.game.find.word.auth.controller;


import com.game.find.word.auth.dto.ResetPasswordRequestDto;
import com.game.find.word.auth.dto.UserDto;
import com.game.find.word.auth.dto.UserUpdateDto;
import com.game.find.word.auth.service.UserService;
import com.game.find.word.base.constant.ApiPathConstant;
import com.game.find.word.base.model.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPathConstant.BASE_PATH+ "user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<BaseResponse<UserDto>> getMe( ) {
        return ResponseEntity.ok(userService.getMe());
    }

    @PutMapping("/update-me")
    public ResponseEntity<BaseResponse<UserDto>> updateMe(
            @RequestBody UserUpdateDto request) {
        return ResponseEntity.ok(userService.updateMe(request));
    }

    @PostMapping("/change-myPassword")
    public ResponseEntity<BaseResponse<Boolean>> changePassword(
            @RequestBody ResetPasswordRequestDto request) {
        return ResponseEntity.ok(userService.changePassword(request));
    }
}