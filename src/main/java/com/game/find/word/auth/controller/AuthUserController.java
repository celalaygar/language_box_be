package com.game.find.word.auth.controller;


import com.game.find.word.auth.dto.ResetPasswordRequestDto;
import com.game.find.word.auth.dto.UserDto;
import com.game.find.word.auth.dto.UserUpdateDto;
import com.game.find.word.auth.model.User;
import com.game.find.word.auth.service.UserService;
import com.game.find.word.base.model.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/user")
public class AuthUserController {

    @Autowired
    private UserService userService;


    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> response = userService.findAll();
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
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