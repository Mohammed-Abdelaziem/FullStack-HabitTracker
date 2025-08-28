package com.example.Daily.Habit.security;
import java.util.Collections;

import com.example.Daily.Habit.errorhandler.AppException;
import com.example.Daily.Habit.errorhandler.ErrorCode;
import com.example.Daily.Habit.errorhandler.ErrorSource;
import org.springframework.security.core.GrantedAuthority;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Daily.Habit.entity.Role;
import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.repository.UserRepository;

//import io.jsonwebtoken.lang.Collections;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
            user.getUserId().toString(),
            user.getEmail(),
            user.getPassword(),
            Collections.<GrantedAuthority>emptyList()
    );
}

    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> AppException.create(
                        ErrorSource.SERVICE,
                        ErrorCode.E404_NOT_FOUND,
                        "User not found",
                        ErrorCode.E404_NOT_FOUND.getStatus()
                ));

                System.out.println("We have User with email: " + user.getEmail());
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().toString())
                .build();
    }

    public String getUserIdFromEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(user -> user.getUserId().toString())
                .orElse(null);
    }
}