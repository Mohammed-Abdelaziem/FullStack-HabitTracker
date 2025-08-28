package com.example.Daily.Habit.controller;

import com.example.Daily.Habit.errorhandler.AppException;
import com.example.Daily.Habit.errorhandler.ErrorCode;
import com.example.Daily.Habit.errorhandler.ErrorSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.example.Daily.Habit.api.UserApi;
import com.example.Daily.Habit.service.UserService;
import com.example.Daily.Habit.mapper.UserMapper;

import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.model.UserPublicResponse;
import com.example.Daily.Habit.model.UserUpdateRequest;
import com.example.Daily.Habit.model.MessageResponse;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class UserController implements UserApi {

    private final UserService userService;
    private final UserMapper userMapper;



    @Override
    public ResponseEntity<List<UserPublicResponse>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserPublicResponse> dto = users.stream().map(userMapper::toPublic).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<UserPublicResponse> userSearchPhoneGet(String phone) {
        User user = userService.getByPhoneNumber(phone);
        return ResponseEntity.ok(userMapper.toPublic(user));
    }


    ////////////////////////////////////////////////////////
    @Override
    public ResponseEntity<UserPublicResponse> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByEmail(email);
        return ResponseEntity.ok(userMapper.toPublic(user));
    }
    @Override
    public ResponseEntity<MessageResponse> updateCurrentUser(UserUpdateRequest userUpdateRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByEmail(email);
        userMapper.patchFromRequest(userUpdateRequest, user);
        userService.save(user);
        return ResponseEntity.ok(new MessageResponse().message("User updated successfully"));
    }

    @Override
    public ResponseEntity<MessageResponse> deleteCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByEmail(email);
        userService.delete(user.getUserId());
        return ResponseEntity.ok(new MessageResponse().message("User deleted successfully"));
    }

}
