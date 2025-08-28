package com.example.Daily.Habit.controller;

import com.example.Daily.Habit.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Daily.Habit.api.HabitApi;
import com.example.Daily.Habit.entity.Habit;
import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.errorhandler.AppException;
import com.example.Daily.Habit.errorhandler.ErrorCode;
import com.example.Daily.Habit.errorhandler.ErrorSource;
import com.example.Daily.Habit.service.HabitService;
import com.example.Daily.Habit.service.UserService;
import com.example.Daily.Habit.mapper.HabitMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@RestController
public class HabitController implements HabitApi {

    private final HabitService habitService;
    private final UserService userService;
    private final HabitMapper habitMapper;

    @Override
    public ResponseEntity<HabitPublicResponse> createHabit(HabitCreateRequest habitCreateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_NOT_FOUND,
                        "User With Email given not found",
                        ErrorCode.E404_NOT_FOUND.getStatus()
                ));

        Habit habit = habitMapper.toEntity(habitCreateRequest);
        habit.setUser(user);
        Habit saved = habitService.save(habit);
        return new ResponseEntity<>(habitMapper.toPublicResponse(saved), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MessageResponse> deleteHabit(String  id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_NOT_FOUND,
                        "User With Email given not found",
                        ErrorCode.E404_NOT_FOUND.getStatus()
                ));
        Habit habit = habitService.findById(id);
        if (habit == null || !habit.getUser().getUserId().equals(user.getUserId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        habitService.delete(id);
        return ResponseEntity.ok(new MessageResponse().message("Habit deleted successfully"));
    }

    @Override
    public ResponseEntity<HabitDetailsResponse> getHabitById(String  id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_NOT_FOUND,
                        "User With Email given not found",
                        ErrorCode.E404_NOT_FOUND.getStatus()
                ));
        Habit habit = habitService.findById(id);
        if (habit == null || !habit.getUser().getUserId().equals(user.getUserId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        HabitDetailsResponse res = habitMapper.toDetailsResponse(habit);
        return ResponseEntity.ok(res);
    }

    @Override
    public ResponseEntity<List<HabitPublicResponse>> getHabits() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_NOT_FOUND,
                        "User With Email given not found",
                        ErrorCode.E404_NOT_FOUND.getStatus()
                ));
        List<Habit> habits = habitService.getHabitsByUser(user.getUserId());
        List<HabitPublicResponse> dto = habits.stream()
                .map(habitMapper::toPublicResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<HabitUpdateResponse> updateHabit(String  id, HabitUpdateRequest habitUpdateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_NOT_FOUND,
                        "User With Email given not found",
                        ErrorCode.E404_NOT_FOUND.getStatus()
                ));
        Habit habit = habitService.findById(id);
        if (habit == null || !habit.getUser().getUserId().equals(user.getUserId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            habitMapper.updateEntityFromRequest(habitUpdateRequest, habit);
            Habit saved = habitService.save(habit);
            HabitUpdateResponse res = new HabitUpdateResponse()
                    .message("Habit updated successfully")
                    .name(saved.getName())
                    .target(saved.getTarget())
                    .frequency(saved.getFrequency() != null
                            ? HabitUpdateResponse.FrequencyEnum.valueOf(saved.getFrequency().name())
                            : null);
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new HabitUpdateResponse().message("Update failed: " + ex.getMessage()));
        }
    }
    @Override
    public ResponseEntity<ProgressResponse> progressGet(UUID habitId, String status) {
        try {
            ProgressResponse response = habitService.calculateProgress(habitId, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            ProgressResponse errorResponse = new ProgressResponse();
            errorResponse.setProgressPercentage("0%");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }



}
