package com.example.Daily.Habit.dto;

import java.util.UUID;

public class ProgressResponseDTO {
    private UUID habitId;
    private String status;
    private String progressPercentage;

    public UUID getHabitId() {
        return habitId;
    }
    public void setHabitId(UUID habitId) {
        this.habitId = habitId;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getProgressPercentage() {
        return progressPercentage;
    }
    public void setProgressPercentage(String progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}
