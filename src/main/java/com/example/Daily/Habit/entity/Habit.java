package com.example.Daily.Habit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.engine.internal.Cascade;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency; // daily, weekly, monthly, etc.

    @Column(nullable = false)
    private Integer target; // number of times per frequency period

    @Column(nullable = false)
    private Double progress = 0.0; // percentage of completion (0.0 to 100.0)

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy ="habit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OngoingHabit> ongoingHabits;
}