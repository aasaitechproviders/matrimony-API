package com.matrimony.matrimony.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing JWT token after login")
public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}