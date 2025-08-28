package com.example.Daily.Habit.errorhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    E400_INVALID_INCREMENT(HttpStatus.BAD_REQUEST),
    E400_INVALID_UPDATE(HttpStatus.BAD_REQUEST),
    E400_INVALID_STATUS(HttpStatus.BAD_REQUEST),
    E400_INVALID_TIMING(HttpStatus.BAD_REQUEST),
    E403_ONGOING_HABIT_NOT_BELONG_TO_USER(HttpStatus.FORBIDDEN),
    E403_HABIT_NOT_BELONG_TO_USER(HttpStatus.FORBIDDEN),
    E404_NOT_FOUND(HttpStatus.NOT_FOUND),
    E404_USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    E404_HABIT_NOT_FOUND(HttpStatus.NOT_FOUND),
    E404_ONGOING_HABIT_NOT_FOUND(HttpStatus.NOT_FOUND),
    E409_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT),
    E409_PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT);
    private final HttpStatus status;
}
