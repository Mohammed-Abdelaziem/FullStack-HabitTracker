package com.example.Daily.Habit.service;

import com.example.Daily.Habit.errorhandler.AppException;
import com.example.Daily.Habit.errorhandler.ErrorCode;
import com.example.Daily.Habit.errorhandler.ErrorSource;
import com.example.Daily.Habit.model.OngoingHabitCreateRequest;
import com.example.Daily.Habit.model.OngoingHabitUpdateRequest;
import com.example.Daily.Habit.entity.Habit;
import com.example.Daily.Habit.entity.OngoingHabit;
import com.example.Daily.Habit.entity.OngoingHabitStatus;
import com.example.Daily.Habit.entity.Frequency;
import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.repository.HabitRepository;
import com.example.Daily.Habit.repository.OngoingHabitRepository;
import com.example.Daily.Habit.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class OngoingHabitService implements GenericService<OngoingHabit> {

    private final OngoingHabitRepository ongoingHabitRepository;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;

    public OngoingHabitService(
            OngoingHabitRepository ongoingHabitRepository,
            UserRepository userRepository,
            HabitRepository habitRepository
    ) {
        this.ongoingHabitRepository = ongoingHabitRepository;
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
    }

    // ---------------- GenericService ----------------
    public UUID parseHabitIdOrThrow(String rawId) {
        try {
            return UUID.fromString(rawId);
        } catch (IllegalArgumentException ex) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E404_HABIT_NOT_FOUND,
                    "Ongoing habit not found with ID " + rawId,
                    ErrorCode.E404_HABIT_NOT_FOUND.getStatus()
            );
        }
    }
    private OngoingHabit getHabitOrThrow(UUID id) {
        return ongoingHabitRepository.findById(id).orElseThrow(() ->
                AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_HABIT_NOT_FOUND,
                        "Ongoing habit not found with ID " + id,
                        ErrorCode.E404_HABIT_NOT_FOUND.getStatus()
                )
        );
    }

    @Override
    public OngoingHabit findById(UUID id) {
        return getHabitOrThrow(id);
    }
    public OngoingHabit findById(String rawId) {
        return getHabitOrThrow(parseHabitIdOrThrow(rawId));
    }

    @Override
    public OngoingHabit save(OngoingHabit entity) {
        if (entity.getUser() == null || entity.getHabit() == null) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E400_INVALID_UPDATE,
                    "OngoingHabit must be linked to a valid User and Habit",
                    HttpStatus.BAD_REQUEST
            );
        }
        return ongoingHabitRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        OngoingHabit ongoingHabit = findById(id);
        ongoingHabitRepository.delete(ongoingHabit);
    }

    @Override
    public Collection<OngoingHabit> findAll() {
        return ongoingHabitRepository.findAll();
    }

    @Override
    public Page<OngoingHabit> findAll(int page, int size) {
        return ongoingHabitRepository.findAll(PageRequest.of(page, size));
    }

    // ---------------- Custom Methods ----------------

    public List<OngoingHabit> findByUserId(UUID userId) {
        return ongoingHabitRepository.findByUser_UserId(userId);
    }


    private void authorizeOngoingHabit(UUID userId, OngoingHabit ongoingHabit) {
        if (!ongoingHabit.getUser().getUserId().equals(userId)) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E403_ONGOING_HABIT_NOT_BELONG_TO_USER,
                    "Ongoing habit does not belong to the specified user",
                    ErrorCode.E403_ONGOING_HABIT_NOT_BELONG_TO_USER.getStatus()
            );
        }
    }



    // Find ongoing habit by user ID and ongoing habit ID
    public OngoingHabit findByUserIdAndId(UUID userId, UUID id) {
        OngoingHabit ongoingHabit = findById(id);
        authorizeOngoingHabit(userId, ongoingHabit);
        return ongoingHabit;
    }


    public OngoingHabit createOngoingHabit(UUID userId, OngoingHabitCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_USER_NOT_FOUND,
                        "User not found with ID: " + userId,
                        ErrorCode.E404_USER_NOT_FOUND.getStatus()
                ));

        Habit habit = habitRepository.findById(request.getHabitId())
                .orElseThrow(() -> AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_HABIT_NOT_FOUND,
                        "Habit not found with ID: " + request.getHabitId(),
                        ErrorCode.E404_HABIT_NOT_FOUND.getStatus()
                ));

        if (!habit.getUser().getUserId().equals(userId)) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E403_HABIT_NOT_BELONG_TO_USER,
                    "Habit does not belong to this user",
                    ErrorCode.E403_HABIT_NOT_BELONG_TO_USER.getStatus()
            );
        }

        OngoingHabit ongoingHabit = new OngoingHabit();
        ongoingHabit.setUser(user);
        ongoingHabit.setHabit(habit);
        ongoingHabit.setStartDate(request.getStartDate());
        ongoingHabit.setStatus(OngoingHabitStatus.NOT_STARTED);
        ongoingHabit.setCounter(0);

        return ongoingHabitRepository.save(ongoingHabit);
    }


    public OngoingHabit updateOngoingHabit(UUID userId, UUID id, OngoingHabitUpdateRequest request) {
        OngoingHabit ongoingHabit = findByUserIdAndId(userId, id);

        if (ongoingHabit.getStatus() != OngoingHabitStatus.NOT_STARTED) {
            throw new IllegalStateException("Cannot update ongoing habit that has already started");
        }

        if (request.getStartDate() != null) {
            ongoingHabit.setStartDate(request.getStartDate());
        }

        return ongoingHabitRepository.save(ongoingHabit);
    }


    public void delete(UUID userId, UUID id) {
        OngoingHabit ongoingHabit = findByUserIdAndId(userId, id);
        ongoingHabitRepository.delete(ongoingHabit);
    }

    // Check off a habit - increment counter and update status based on target completion

    public OngoingHabit checkOffHabit(UUID userId, UUID ongoingHabitId, int incrementBy) {
        if (incrementBy <= 0) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E400_INVALID_INCREMENT,
                    "Increment must be greater than 0",
                    ErrorCode.E400_INVALID_INCREMENT.getStatus()
            );
        }

        OngoingHabit ongoingHabit = findById(ongoingHabitId);
        authorizeOngoingHabit(userId, ongoingHabit);

        // Validate that the habit can be checked off
        validateHabitCanBeCheckedOff(ongoingHabit);

        // Validate timing based on frequency
        validateUpdateTiming(ongoingHabit);

        // Update counter and status
        return updateCounterAndStatus(ongoingHabit, incrementBy);
    }



    private void validateHabitCanBeCheckedOff(OngoingHabit ongoingHabit) {
        OngoingHabitStatus status = ongoingHabit.getStatus();

        if (status == OngoingHabitStatus.COMPLETED) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E400_INVALID_STATUS,
                    "Cannot check off a habit that is already completed",
                    ErrorCode.E400_INVALID_STATUS.getStatus()
            );
        }

        if (status == OngoingHabitStatus.INCOMPLETE) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E400_INVALID_STATUS,
                    "Cannot check off a habit that is marked as incomplete",
                    ErrorCode.E400_INVALID_STATUS.getStatus()
            );
        }

        if (status == OngoingHabitStatus.NOT_STARTED) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E400_INVALID_STATUS,
                    "Cannot check off a habit before its start date",
                    ErrorCode.E400_INVALID_STATUS.getStatus()
            );
        }
    }


    private void validateUpdateTiming(OngoingHabit ongoingHabit) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = ongoingHabit.getStartDate();
        Frequency frequency = ongoingHabit.getHabit().getFrequency();

        switch (frequency) {
            case DAILY:
                if (!today.equals(startDate)) {
                    throw AppException.create(
                            ErrorSource.SERVICE,
                            ErrorCode.E400_INVALID_TIMING,
                            "Daily habits can only be checked off on the start date",
                            ErrorCode.E400_INVALID_TIMING.getStatus()
                    );
                }
                break;

            case WEEKLY:
                long daysSinceStart = ChronoUnit.DAYS.between(startDate, today);
                if (daysSinceStart < 0 || daysSinceStart >= 7) {
                    throw AppException.create(
                            ErrorSource.SERVICE,
                            ErrorCode.E400_INVALID_TIMING,
                            "Weekly habits can only be checked off within 7 days of the start date",
                            ErrorCode.E400_INVALID_TIMING.getStatus()
                    );
                }
                break;

            case MONTHLY:
                long noOfDaysSinceStart = ChronoUnit.DAYS.between(startDate, today);
                if (noOfDaysSinceStart < 0 || noOfDaysSinceStart >= 30) {
                    throw AppException.create(
                            ErrorSource.SERVICE,
                            ErrorCode.E400_INVALID_TIMING,
                            "Monthly habits can only be checked off within 30 days of the start date",
                            ErrorCode.E400_INVALID_TIMING.getStatus()
                    );
                }
                break;

            default:
                throw AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E400_INVALID_TIMING,
                        "Unsupported frequency: " + frequency,
                        ErrorCode.E400_INVALID_TIMING.getStatus()
                );
        }
    }


    private OngoingHabit updateCounterAndStatus(OngoingHabit ongoingHabit, int incrementBy) {
        int counter = ongoingHabit.getCounter();
        counter += incrementBy;
        ongoingHabit.setCounter(counter);

        int target = ongoingHabit.getHabit().getTarget();
        if (counter >= target) {
            ongoingHabit.setStatus(OngoingHabitStatus.COMPLETED);
        } else {
            ongoingHabit.setStatus(OngoingHabitStatus.IN_PROGRESS);
        }

        return ongoingHabitRepository.save(ongoingHabit);
    }

}