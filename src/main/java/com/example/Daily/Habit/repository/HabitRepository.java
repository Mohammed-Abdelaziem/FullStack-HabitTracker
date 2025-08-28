package com.example.Daily.Habit.repository;

import com.example.Daily.Habit.entity.Habit;
import com.example.Daily.Habit.entity.Frequency;
import com.example.Daily.Habit.entity.OngoingHabitStatus;
import com.example.Daily.Habit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HabitRepository extends JpaRepository<Habit, UUID> {

    List<Habit> findByUser_UserId(UUID userUserId);

    List<Habit> findByUser(com.example.Daily.Habit.entity.User user);

    long countByUser(com.example.Daily.Habit.entity.User user);

    long countByUserAndProgressGreaterThan(User user, Double progress);
    @Query("SELECT COUNT(oh) FROM OngoingHabit oh WHERE oh.habit.id = :habitId")
    long countOngoingStatusForHabit(@Param("habitId") UUID habitId);

    @Query("SELECT COUNT(oh) FROM OngoingHabit oh WHERE oh.habit.id = :habitId AND oh.status = :status")
    long countOngoingStatusForHabitAndStatus(@Param("habitId") UUID habitId, @Param("status") OngoingHabitStatus status);

    @Query("SELECT COUNT(oh) FROM OngoingHabit oh WHERE oh.status = :status")
    long countByStatus(@Param("status") OngoingHabitStatus status);

    @Query("SELECT COUNT(oh) FROM OngoingHabit oh")
    long countAllOngoing();

    @Query("SELECT COUNT(oh) FROM OngoingHabit oh WHERE oh.habit.id = :habitId AND oh.status = com.example.Daily.Habit.entity.OngoingHabitStatus.COMPLETED")
    long countCompletedForHabit(@Param("habitId") UUID habitId);

    @Query("SELECT COUNT(oh) FROM OngoingHabit oh WHERE oh.habit.id = :habitId")
    long countTotalForHabit(@Param("habitId") UUID habitId);
}
