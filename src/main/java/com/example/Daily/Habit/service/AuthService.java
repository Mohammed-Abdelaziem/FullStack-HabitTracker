package com.example.Daily.Habit.service;

import com.example.Daily.Habit.dto.RegisterRequest;
import com.example.Daily.Habit.entity.Role;
import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.errorhandler.AppException;
import com.example.Daily.Habit.errorhandler.ErrorCode;
import com.example.Daily.Habit.errorhandler.ErrorSource;
import com.example.Daily.Habit.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest req) {

        final String email = req.getEmail().trim().toLowerCase(Locale.ROOT);
        final String phone = req.getPhoneNumber().trim();

        if (userRepository.existsByEmail(req.getEmail())) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E409_EMAIL_ALREADY_EXISTS,
                    "An account with this email already exists.",
                    HttpStatus.CONFLICT
            );
        }
        if (userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw AppException.create(
                    ErrorSource.SERVICE,
                    ErrorCode.E409_PHONE_ALREADY_EXISTS,
                    "An account with this phone number already exists.",
                    HttpStatus.CONFLICT
            );
        }

        User u = new User();
        u.setName(req.getName());
        u.setEmail(email);
        u.setPhoneNumber(phone);
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(Role.USER);

        return userRepository.save(u);
    }
}
