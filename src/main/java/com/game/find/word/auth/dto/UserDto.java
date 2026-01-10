package com.game.find.word.auth.dto;


import com.game.find.word.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private Date birthdate;

    public UserDto(User user) {
        this.id= user.getId();
        this.firstname= user.getFirstname();
        this.lastname= user.getLastname();
        this.email= user.getEmail();
        this.birthdate= user.getBirthdate();
    }
}