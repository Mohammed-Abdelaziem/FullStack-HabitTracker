package com.example.Daily.Habit.controller;


import com.example.Daily.Habit.errorhandler.AppException;
import com.example.Daily.Habit.errorhandler.ErrorCode;
import com.example.Daily.Habit.errorhandler.ErrorSource;
import com.example.Daily.Habit.mapper.UserMapper;
import com.example.Daily.Habit.model.UserPublicResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.Daily.Habit.dto.LoginRequest;
import com.example.Daily.Habit.dto.RegisterRequest;
import com.example.Daily.Habit.entity.Role;
import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.security.CustomUserDetails;
import com.example.Daily.Habit.security.JwtUtil;
import com.example.Daily.Habit.service.UserService;
import com.example.Daily.Habit.service.AuthService;
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserMapper userMapper;



    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
       CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
       // System.out.println("User ID: " + userDetails.getUserId() + ", Email " + userDetails.getUsername() +", password " + userDetails.getPassword() + " Will generate token for him now");
       return jwtUtil.generateToken(userDetails.getUserId());
    }

    @PostMapping("/register")
    public ResponseEntity<UserPublicResponse> register(@Valid @RequestBody RegisterRequest request) {
        User saved = authService.register(request);
        UserPublicResponse body = userMapper.toPublic(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}