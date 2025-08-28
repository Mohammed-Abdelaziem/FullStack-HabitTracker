package com.example.Daily.Habit.controller;

import com.example.Daily.Habit.service.HabitSchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduler")
public class HabitSchedulerController {

    private final HabitSchedulerService habitSchedulerService;

    public HabitSchedulerController(HabitSchedulerService habitSchedulerService) {
        this.habitSchedulerService = habitSchedulerService;
    }


     // Manually trigger the habit status update process for testing


    @PostMapping("/trigger")
    public ResponseEntity<String> triggerScheduler() {
        try {   
            habitSchedulerService.triggerManualUpdate();
            return ResponseEntity.ok("Habit status update triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error triggering habit status update: " + e.getMessage());
        }
    }
}