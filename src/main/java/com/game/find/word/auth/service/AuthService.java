package com.game.find.word.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.game.find.word.auth.dto.OAuth2LoginRequest;
import com.game.find.word.auth.dto.RegisterRequest;
import com.game.find.word.auth.model.Role;
import com.game.find.word.auth.model.User;
import com.game.find.word.auth.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppleIdTokenVerifier appleIdTokenVerifier; // Add this line
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String appleClientId;

    public User register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Bu email adresi zaten kullanılıyor.");
        }
        User newUser = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(Role.USER))
                .provider("LOCAL")
                .build();
        return userRepository.save(newUser);
    }

    public User loginWithGoogle(OAuth2LoginRequest request) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(request.getIdToken());
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String providerId = payload.getSubject();
            String displayName = (String) payload.get("name");

            Optional<User> existingUser = userRepository.findByProviderIdAndProvider(providerId, "GOOGLE");
            if (existingUser.isPresent()) {
                return existingUser.get();
            } else {
                User newUser = User.builder()
                        .email(email)
                        .displayName(displayName)
                        .provider("GOOGLE")
                        .providerId(providerId)
                        .roles(Set.of(Role.USER))
                        .build();
                return userRepository.save(newUser);
            }
        }
        throw new RuntimeException("Google token doğrulama hatası.");
    }

    public User loginWithApple(OAuth2LoginRequest request) {
        try {
            DecodedJWT jwt = appleIdTokenVerifier.verify(request.getIdToken(), appleClientId);
            String providerId = jwt.getSubject();
            String email = jwt.getClaim("email").asString();
            String displayName = email != null ? email.substring(0, email.indexOf("@")) : "Apple User"; // E-posta varsa ad al

            Optional<User> existingUser = userRepository.findByProviderIdAndProvider(providerId, "APPLE");
            if (existingUser.isPresent()) {
                return existingUser.get();
            } else {
                User newUser = User.builder()
                        .email(email)
                        .displayName(displayName)
                        .provider("APPLE")
                        .providerId(providerId)
                        .roles(Set.of(Role.USER))
                        .build();
                return userRepository.save(newUser);
            }
        } catch (Exception e) {
            throw new RuntimeException("Apple token doğrulama hatası: " + e.getMessage());
        }
    }
}