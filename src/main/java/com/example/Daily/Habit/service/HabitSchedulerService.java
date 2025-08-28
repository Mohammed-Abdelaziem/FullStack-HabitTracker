package com.example.Daily.Habit.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Daily.Habit.entity.Frequency;
import com.example.Daily.Habit.entity.OngoingHabit;
import com.example.Daily.Habit.entity.OngoingHabitStatus;
import com.example.Daily.Habit.repository.OngoingHabitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class HabitSchedulerService {

    private final OngoingHabitRepository ongoingHabitRepository;

    public HabitSchedulerService(OngoingHabitRepository ongoingHabitRepository) {
        this.ongoingHabitRepository = ongoingHabitRepository;
    }


    @Scheduled(cron = "0 0 0 * * *") // Runs at midnight every day
    @Transactional
    public void updateHabitStatuses() {
        log.info("Starting daily habit status update at {}", LocalDate.now());

        try {
            updateNotStartedToStarted();
            updateExpiredHabitsToIncomplete();
            log.info("Daily habit status update completed successfully");
        } catch (Exception e) {
            log.error("Error occurred during daily habit status update", e);
        }
    }


     // Update habits from NOT_STARTED to STARTED when start date is reached

    private void updateNotStartedToStarted() {
        LocalDate today = LocalDate.now();

        List<OngoingHabit> habitsToStart = ongoingHabitRepository.findByStatus(OngoingHabitStatus.NOT_STARTED)
                .stream()
                .filter(habit -> habit.getStartDate().isBefore(today) || habit.getStartDate().isEqual(today) )
                .toList();

        log.info("Found {} habits to start today", habitsToStart.size());

        for (OngoingHabit habit : habitsToStart) {
            habit.setStatus(OngoingHabitStatus.STARTED);
            ongoingHabitRepository.save(habit);
            log.debug("Updated habit {} to STARTED status", habit.getId());
        }
    }


     // Update habits to INCOMPLETE when their time window has expired

    private void updateExpiredHabitsToIncomplete() {
        LocalDate today = LocalDate.now();

        List<OngoingHabit> activeHabits = ongoingHabitRepository.findByStatusIn(
                List.of(OngoingHabitStatus.STARTED, OngoingHabitStatus.IN_PROGRESS)
        );

        log.info("Checking {} active habits for expiration", activeHabits.size());

        for (OngoingHabit habit : activeHabits) {
            if (isHabitExpired(habit, today)) {
                habit.setStatus(OngoingHabitStatus.INCOMPLETE);
                ongoingHabitRepository.save(habit);
                log.debug("Updated habit {} to INCOMPLETE status", habit.getId());
            }
        }
    }


     // Check if a habit's time window has expired based on its frequency

    private boolean isHabitExpired(OngoingHabit habit, LocalDate today) {
        LocalDate startDate = habit.getStartDate();
        Frequency frequency = habit.getHabit().getFrequency();
        long daysSinceStart = ChronoUnit.DAYS.between(startDate, today);

        return switch (frequency) {
            case DAILY -> daysSinceStart > 0;
            case WEEKLY -> daysSinceStart >= 7;
            case MONTHLY -> daysSinceStart >= 30;
        };
    }




     // Manual trigger method for testing purposes

    public void triggerManualUpdate() {
        log.info("Manual habit status update triggered");
        updateHabitStatuses();
    }


}