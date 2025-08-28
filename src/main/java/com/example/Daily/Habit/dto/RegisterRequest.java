package com.example.Daily.Habit.dto;

import org.hibernate.validator.constraints.UniqueElements;

import com.example.Daily.Habit.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.websocket.server.PathParam;

public class RegisterRequest {
    @NotBlank
    private String name;
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 8 characters long and contain letters and numbers")
    private String password;
    @NotBlank
    @Pattern(regexp = "^[0-9]{11}$", message = "Phone number must be 11 digits")
    private String phoneNumber;
    @NotBlank
    @Email
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.com$", message = "Invalid email format")
    private String email;

    // Getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}