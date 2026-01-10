package com.game.find.word.auth.dto;


import lombok.Data;

@Data
public class OAuth2LoginRequest {
    private String provider; // "google" veya "apple"
    private String idToken; // Google/Apple tarafından verilen idToken
}