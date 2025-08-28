package com.example.Daily.Habit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
public class CustomError {
    private int status;
    private String error;
    private String message;
}