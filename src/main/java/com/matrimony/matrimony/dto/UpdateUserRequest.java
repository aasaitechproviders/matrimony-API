package com.matrimony.matrimony.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Update user profile request")
public class UpdateUserRequest {
    private String phone;
    private String firstName;
    private String lastName;

    // add more profile fields as needed

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}