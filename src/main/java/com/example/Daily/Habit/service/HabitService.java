package com.example.Daily.Habit.service;

import com.example.Daily.Habit.dto.ProgressResponseDTO;
import com.example.Daily.Habit.entity.Frequency;
import com.example.Daily.Habit.entity.Habit;
import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.entity.OngoingHabitStatus;
import com.example.Daily.Habit.errorhandler.AppException;
import com.example.Daily.Habit.errorhandler.ErrorCode;
import com.example.Daily.Habit.errorhandler.ErrorSource;
import com.example.Daily.Habit.mapper.HabitMapper;
import com.example.Daily.Habit.model.ProgressResponse;
import com.example.Daily.Habit.repository.HabitRepository;
import com.example.Daily.Habit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HabitService implements GenericService<Habit> {
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final HabitMapper habitMapper;


    public UUID parseHabitIdOrThrow(String rawId) {
        try {
            return UUID.fromString(rawId);
        } catch (IllegalArgumentException ex) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E404_HABIT_NOT_FOUND,
                    "Habit not found with ID: " + rawId,
                    ErrorCode.E404_HABIT_NOT_FOUND.getStatus()
            );
        }
    }
    private Habit getHabitOrThrow(UUID id) {
        return habitRepository.findById(id).orElseThrow(() ->
                AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_HABIT_NOT_FOUND,
                        "Habit not found with ID: " + id,
                        ErrorCode.E404_HABIT_NOT_FOUND.getStatus()
                )
        );
    }


    @Override
    public Habit findById(UUID id) {
        return getHabitOrThrow(id);
    }
    public Habit findById(String rawId) {
        return getHabitOrThrow(parseHabitIdOrThrow(rawId));
    }

    @Override
    public Habit save(Habit habit) {
        if (habit.getUser() == null || habit.getUser().getUserId() == null) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E404_NOT_FOUND,
                    "Habit must be linked to a valid User",
                    ErrorCode.E404_NOT_FOUND.getStatus()
            );
        }
        return habitRepository.save(habit);
    }

    @Override
    public void delete(UUID id) {
        Habit habit = findById(id);
        habitRepository.delete(habit);
    }
    public void delete(String rawId) {
        delete(parseHabitIdOrThrow(rawId));
    }

    @Override
    public Collection<Habit> findAll() {
        return habitRepository.findAll();
    }

    @Override
    public Page<Habit> findAll(int page, int size) {
        return habitRepository.findAll(PageRequest.of(page, size));
    }


    public List<Habit> getHabitsByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_NOT_FOUND,
                        "User not found",
                        ErrorCode.E404_NOT_FOUND.getStatus()
                ));
        return habitRepository.findByUser(user);
    }

    //for progress calculation
    public long countCompletedHabits(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_NOT_FOUND,
                        "User not found",
                        ErrorCode.E404_NOT_FOUND.getStatus()
                ));
        return habitRepository.countByUserAndProgressGreaterThan(user, 0.0);
    }

    public long countHabitsByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_NOT_FOUND,
                        "User not found",
                        ErrorCode.E404_NOT_FOUND.getStatus()
                ));
        return habitRepository.countByUser(user);
    }
    // Check active ongoing habits with status STARTED or IN_PROGRESS
    public boolean hasOngoingHabitsWithActiveStatus(UUID habitId) {
        Habit habit = findById(habitId);
        return habit.getOngoingHabits().stream()
                .anyMatch(ongoingHabit -> ongoingHabit.getStatus() == OngoingHabitStatus.STARTED
                        || ongoingHabit.getStatus() == OngoingHabitStatus.IN_PROGRESS);
    }

    // Update habit’s allowed fields only if no active ongoing habits
    public Habit updateHabit(UUID habitId, String name, Integer target, String frequency) {
        Habit habit = findById(habitId);

        if (hasOngoingHabitsWithActiveStatus(habitId)) {
            throw new IllegalStateException("Cannot update habit - has ongoing habits with STARTED or IN_PROGRESS status");
        }

        if (name != null) habit.setName(name);
        if (target != null) habit.setTarget(target);
        if (frequency != null) habit.setFrequency(Frequency.valueOf(frequency));

        return habitRepository.save(habit);
    }

    public ProgressResponse calculateProgress(UUID habitId, String status) {
        OngoingHabitStatus filterStatus = null;
        if (status != null) {
            try {
                filterStatus = OngoingHabitStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        }

        if (habitId != null && !habitRepository.existsById(habitId)) {
            throw new IllegalArgumentException("Habit ID does not exist: " + habitId);
        }

        double progressDecimal = 0.0;

        if (habitId != null && filterStatus != null) {
            long total = habitRepository.countOngoingStatusForHabit(habitId);
            long matching = habitRepository.countOngoingStatusForHabitAndStatus(habitId, filterStatus);
            progressDecimal = total == 0 ? 0.0 : (double) matching / total;
        } else if (habitId == null && filterStatus == null) {
            long completed = habitRepository.countByStatus(OngoingHabitStatus.COMPLETED);
            long total = habitRepository.countAllOngoing();
            progressDecimal = total == 0 ? 0.0 : (double) completed / total;
        } else if (habitId == null) {
            long matching = habitRepository.countByStatus(filterStatus);
            long total = habitRepository.countAllOngoing();
            progressDecimal = total == 0 ? 0.0 : (double) matching / total;
        } else {
            long completed = habitRepository.countCompletedForHabit(habitId);
            long total = habitRepository.countTotalForHabit(habitId);
            progressDecimal = total == 0 ? 0.0 : (double) completed / total;
        }

        String progressString = String.format("%.0f%%", progressDecimal * 100);

        ProgressResponse response = new ProgressResponse();
        if (habitId != null) {
            response.setHabitId(JsonNullable.of(habitId));
        } else {
            response.setHabitId(JsonNullable.undefined());
        }
        if (status != null) {
            try {
                ProgressResponse.StatusEnum statusEnum = ProgressResponse.StatusEnum.valueOf(status.toUpperCase());
                response.setStatus(JsonNullable.of(statusEnum));
            } catch (IllegalArgumentException e) {
                response.setStatus(JsonNullable.undefined());
            }
        } else {
            response.setStatus(JsonNullable.undefined());
        }
        response.setProgressPercentage(progressString);

        return response;
    }



    public ProgressResponseDTO toDto(ProgressResponse response) {
        ProgressResponseDTO dto = new ProgressResponseDTO();
        dto.setHabitId(response.getHabitId().orElse(null));
        if (response.getStatus().isPresent()) {
            dto.setStatus(response.getStatus().get().name());
        } else {
            dto.setStatus(null);
        }

        dto.setProgressPercentage(response.getProgressPercentage());
        return dto;
    }
}
