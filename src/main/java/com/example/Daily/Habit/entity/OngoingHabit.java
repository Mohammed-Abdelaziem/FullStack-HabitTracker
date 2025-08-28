package com.example.Daily.Habit.entity;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ongoing_habits")
public class OngoingHabit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // TODO: Local date time

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OngoingHabitStatus status = OngoingHabitStatus.NOT_STARTED;

    @Column(nullable = false)
    private Integer counter = 0;
    // in OngoingHabit.java
    public void setUser(User user) { this.user = user; }
    public void setHabit(Habit habit) { this.habit = habit; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setStatus(OngoingHabitStatus status) { this.status = status; }
    public void setCounter(Integer counter) { this.counter = counter; }
    public Habit getHabit() { return habit; }

    @ManyToOne()
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



}
