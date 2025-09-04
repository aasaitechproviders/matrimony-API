package com.matrimony.matrimony.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request with email and password")
public class LoginRequest {
    private String email;
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
