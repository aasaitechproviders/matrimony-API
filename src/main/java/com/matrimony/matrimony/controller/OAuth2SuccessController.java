package com.matrimony.matrimony.controller;

import com.matrimony.matrimony.entity.User;
import com.matrimony.matrimony.repository.UserRepository;
import com.matrimony.matrimony.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class OAuth2SuccessController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public OAuth2SuccessController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @GetMapping("/auth/oauth2/success")
    public void success(Authentication authentication, HttpServletResponse response) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // ✅ At this point, user is guaranteed in DB
        User user = userRepository.findByEmail(email).orElseThrow();

        // Generate JWT
        String token = jwtUtil.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password("")
                        .authorities("ROLE_" + user.getRole().name())
                        .build()
        );

        // Redirect back to Angular with token
        String redirectUrl = "http://localhost:4200/login?token=" + token;
        response.sendRedirect(redirectUrl);
    }

}
