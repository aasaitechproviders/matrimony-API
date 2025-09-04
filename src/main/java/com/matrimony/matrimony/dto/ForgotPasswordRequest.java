package com.matrimony.matrimony.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to initiate forgot password")
public class ForgotPasswordRequest {
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
