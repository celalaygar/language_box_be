package com.game.find.word.auth.security;

import com.game.find.word.base.constant.ApiPathConstant;
import com.game.find.word.base.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                ApiPaths.ScrambledWordCtrl.CTRL + "/**",
                                ApiPaths.VoiceMatchCtrl.CTRL + "/**",
                                ApiPaths.KeywordQuizCtrl.CTRL + "/**",
                                ApiPaths.SentenceCtrl.CTRL + "/**",
                                ApiPaths.SentenceBuilderCtrl.CTRL + "/**",
                                ApiPaths.GlobalCtrl.CTRL + "/**",
                                ApiPaths.VersionCtrl.CTRL + "/**",
                                ApiPaths.GridChallangeCtrl.CTRL + "/**"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(
                                ApiPathConstant.BASE_PATH + "user/**",
                                ApiPaths.VoiceMatchAdminCtrl.CTRL + "/**",
                                ApiPaths.KeywordQuizAdminCtrl.CTRL + "/**",
                                ApiPaths.SentenceAdminCtrl.CTRL + "/**",
                                ApiPaths.SentenceBuilderAdminCtrl.CTRL + "/**",
                                ApiPaths.ScrambledWordAdminCtrl.CTRL + "/**",
                                ApiPaths.VersionAdminCtrl.CTRL + "/**"
                        ).authenticated()
                        .requestMatchers(ApiPathConstant.BASE_PATH + "zodiac-match/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_PASSIVE")
                        .requestMatchers("/api/auth/user/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER", "ROLE_MANAGER", "ROLE_PASSIVE")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(""));
        configuration.setAllowedMethods(Arrays.asList(""));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}