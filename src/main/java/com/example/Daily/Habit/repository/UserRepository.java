package com.example.Daily.Habit.repository;

import com.example.Daily.Habit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {


    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByName(String name);

}
