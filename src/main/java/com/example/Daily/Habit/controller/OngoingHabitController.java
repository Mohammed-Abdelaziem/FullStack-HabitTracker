package com.example.Daily.Habit.controller;

import com.example.Daily.Habit.api.OngoingHabitApi;
import com.example.Daily.Habit.entity.OngoingHabit;
import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.mapper.OngoingHabitApiMapper;
import com.example.Daily.Habit.model.OngoingHabitCreateRequest;
import com.example.Daily.Habit.model.OngoingHabitResponse;
import com.example.Daily.Habit.model.OngoingHabitUpdateRequest;
import com.example.Daily.Habit.model.OngoingHabitCheckOffRequest;
import com.example.Daily.Habit.service.OngoingHabitService;
import com.example.Daily.Habit.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class OngoingHabitController implements OngoingHabitApi {

    private final OngoingHabitService ongoingHabitService;
    private final OngoingHabitApiMapper mapper;
    private final UserService userService;


    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getByEmail(email);
    }

    @Override
    public ResponseEntity<List<OngoingHabitResponse>> getAllOngoingHabitsByUser() {
        User currentUser = getCurrentUser();
        List<OngoingHabit> ongoingHabits = ongoingHabitService.findByUserId(currentUser.getUserId());
        return ResponseEntity.ok(mapper.toApiResponseList(ongoingHabits));
    }

    @Override
    public ResponseEntity<OngoingHabitResponse> createOngoingHabit(OngoingHabitCreateRequest request) {
        User currentUser = getCurrentUser();
        OngoingHabit createdHabit = ongoingHabitService.createOngoingHabit(currentUser.getUserId(), request);
        return new ResponseEntity<>(mapper.toApiResponse(createdHabit), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<OngoingHabitResponse> getOngoingHabitById(String id) {
        User currentUser = getCurrentUser();
        OngoingHabit ongoingHabit = ongoingHabitService.findByUserIdAndId(currentUser.getUserId(),ongoingHabitService.parseHabitIdOrThrow(id));
        return ResponseEntity.ok(mapper.toApiResponse(ongoingHabit));
    }

    @Override
    public ResponseEntity<OngoingHabitResponse> updateOngoingHabit(String id, OngoingHabitUpdateRequest request) {
        User currentUser = getCurrentUser();
        OngoingHabit updated = ongoingHabitService.updateOngoingHabit(currentUser.getUserId(), ongoingHabitService.parseHabitIdOrThrow(id), request);
        return ResponseEntity.ok(mapper.toApiResponse(updated));
    }

    @Override
    public ResponseEntity<Void> deleteOngoingHabit(String id) {
        User currentUser = getCurrentUser();
        ongoingHabitService.delete(currentUser.getUserId(), ongoingHabitService.parseHabitIdOrThrow(id));
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<OngoingHabitResponse> checkOffOngoingHabit(String id, OngoingHabitCheckOffRequest request) {
        User currentUser = getCurrentUser();
        OngoingHabit updated = ongoingHabitService.checkOffHabit(currentUser.getUserId(), ongoingHabitService.parseHabitIdOrThrow(id), request.getIncrement());
        return ResponseEntity.ok(mapper.toApiResponse(updated));
    }
}