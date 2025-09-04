package com.matrimony.matrimony.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reset password using token")
public class ResetPasswordRequest {
    private String email;
    private String token;
    private String newPassword;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}