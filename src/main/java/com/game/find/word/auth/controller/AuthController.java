package com.game.find.word.auth.controller;

import com.game.find.word.auth.dto.AuthResponse;
import com.game.find.word.auth.dto.LoginRequest;
import com.game.find.word.auth.dto.OAuth2LoginRequest;
import com.game.find.word.auth.dto.RegisterRequest;
import com.game.find.word.auth.model.User;
import com.game.find.word.auth.repository.UserRepository;
import com.game.find.word.auth.security.JwtProvider;
import com.game.find.word.auth.service.AuthService;
import com.game.find.word.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider provider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        try {
            logger.info("AuthController register request");
            logger.info(request.getEmail() + " " + request.getPassword());
            User user = authService.register(request);
            logger.info("AuthController register response");
            logger.info(user.getId() + " " + user.getEmail() + " " + user.getPassword());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.info("AuthController register error");
            logger.info(e.getMessage());
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            logger.info("AuthController login request " + new Date());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            String token = provider.generateToken(authentication);
            Optional<User> user = userRepository.findByEmail(request.getEmail());
            logger.info("AuthController login response " + new Date());
            return ResponseEntity.ok(new AuthResponse(token, user.get()));
        } catch (Exception e) {
            logger.info("AuthController login error " + new Date());
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
            throw new RuntimeException("E-posta veya şifre yanlış.");
        }
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestBody OAuth2LoginRequest request) {
        try {
            User user = authService.loginWithGoogle(request);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String token = provider.generateToken(authentication);
            return ResponseEntity.ok(new AuthResponse(token, user));
        } catch (Exception e) {
            throw new RuntimeException("Google ile giriş yapılamadı.");
        }
    }

    @PostMapping("/oauth2/apple")
    public ResponseEntity<AuthResponse> loginWithApple(@RequestBody OAuth2LoginRequest request) {
        try {
            User user = authService.loginWithApple(request);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String token = provider.generateToken(authentication);
            return ResponseEntity.ok(new AuthResponse(token, user));
        } catch (Exception e) {
            throw new RuntimeException("Apple ile giriş yapılamadı: " + e.getMessage());
        }
    }
}