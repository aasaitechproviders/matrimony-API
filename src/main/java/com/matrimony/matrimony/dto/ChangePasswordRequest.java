package com.matrimony.matrimony.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to change password")
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}