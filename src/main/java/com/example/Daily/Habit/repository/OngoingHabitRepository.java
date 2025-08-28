package com.example.Daily.Habit.repository;

import com.example.Daily.Habit.entity.OngoingHabit;
import com.example.Daily.Habit.entity.OngoingHabitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OngoingHabitRepository extends JpaRepository<OngoingHabit, UUID> {

    List<OngoingHabit> findByUser_UserId(UUID userId);

    List<OngoingHabit> findByHabitId(UUID habitId);

    List<OngoingHabit> findByUser_UserIdAndStatus(UUID userId, OngoingHabitStatus status);

    // Find ongoing habit by user ID and ongoing habit ID
    @Query("SELECT oh FROM OngoingHabit oh WHERE oh.user.userId = :userId AND oh.id = :id")
    Optional<OngoingHabit> findByUserIdAndId(@Param("userId") UUID userId, @Param("id") UUID id);

    // Find all habits that should be started (start date <= today and status = NOT_STARTED)
    @Query("SELECT oh FROM OngoingHabit oh WHERE oh.startDate <= :date AND oh.status = :status")
    List<OngoingHabit> findHabitsToStart(@Param("date") LocalDate date, @Param("status") OngoingHabitStatus status);

    // Find habits by status only (for global operations)
    List<OngoingHabit> findByStatus(OngoingHabitStatus status);


     // Find all ongoing habits by multiple statuses
    List<OngoingHabit> findByStatusIn(List<OngoingHabitStatus> statuses);


}

