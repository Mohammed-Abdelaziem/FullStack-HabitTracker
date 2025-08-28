package com.example.Daily.Habit.service;

import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.errorhandler.AppException;
import com.example.Daily.Habit.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.Daily.Habit.errorhandler.ErrorCode.E404_NOT_FOUND;
import static com.example.Daily.Habit.errorhandler.ErrorSource.SERVICE;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                AppException.create(SERVICE, E404_NOT_FOUND, "User not found", HttpStatus.NOT_FOUND)
        );
    }
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() ->
                AppException.create(SERVICE, E404_NOT_FOUND, "User not found", HttpStatus.NOT_FOUND)
        );
    }
    public User getByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() ->
                AppException.create(SERVICE, E404_NOT_FOUND, "User not found", HttpStatus.NOT_FOUND)
        );
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(UUID id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phone) {
        return userRepository.existsByPhoneNumber(phone);
    }
}
