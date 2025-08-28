package com.example.Daily.Habit.errorhandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter

public class AppException extends RuntimeException {
    private final ErrorSource source;
    private final ErrorCode code;
    private final HttpStatus status;

    private AppException(ErrorSource source, ErrorCode code, String message, HttpStatus status) {
        super(message);
        this.source = source;
        this.code = code;
        this.status = status;

    }

    public static AppException create(ErrorSource source, ErrorCode code, String message, HttpStatus status) {
        return new AppException(source, code, message, status);
    }


}
