package com.example.Daily.Habit.controller;

import com.example.Daily.Habit.dto.LoginRequest;
import com.example.Daily.Habit.entity.Frequency;
import com.example.Daily.Habit.entity.Habit;
import com.example.Daily.Habit.entity.Role;
import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.model.HabitCreateRequest;
import com.example.Daily.Habit.repository.HabitRepository;
import com.example.Daily.Habit.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import com.example.Daily.Habit.mapper.HabitMapper;
import com.example.Daily.Habit.model.HabitPublicResponse;
import com.example.Daily.Habit.service.HabitService;
import com.example.Daily.Habit.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HabitControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired  private HabitService habitService;
    @Autowired  private UserService userService;
    @Autowired  private HabitMapper habitMapper;
    @Autowired private HabitRepository habitRepository;
    @Autowired private UserRepository userRepository;

    @Autowired private HabitController habitController;

    private String jwtToken;
    private User user;
    private Habit habit1;
    private Habit habit2;
    private UUID createdHabitId;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Ensure a test user exists
        if (userRepository.findByEmailIgnoreCase("test@example.com").isEmpty()) {
            user = new User();
            user.setEmail("test@example.com");
            user.setPassword("$2a$10$sGwbNiaMuAzUxCXHmjRwvefHLl.6foizxz4aIvq/l2JxPA8l.F0fO");
            user.setName("Test User");
            user.setPhoneNumber("01000002345");
            user.setRole(Role.USER);
            userRepository.save(user);
        }
        /// Habit1 and Habit2 For user
        habit1 = new Habit();
        habit1.setName("Drink Water");
        habit1.setFrequency(Frequency.DAILY);
        habit1.setTarget(8);
        habit1.setProgress(0.00);
        habit1.setUser(user);
        habitRepository.save(habit1);
        createdHabitId = habit1.getId();
        habit2 = new Habit();
        habit2.setName("Running");
        habit2.setFrequency(Frequency.DAILY);
        habit2.setTarget(8);
        habit2.setProgress(0.00);
        habit2.setUser(user);
        habitRepository.save(habit2);

        // 2. Perform login and capture JWT
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("admin1234");

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "/auth/login",
                loginRequest,
                String.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 3. Extract token from JSON response
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.jwtToken = loginResponse.getBody();
            System.out.println("Token: "+ this.jwtToken);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract JWT token", e);
        }
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        userRepository.deleteAll();
        habitRepository.deleteAll();
    }

    @Test
    void shouldCreateHabitForUser() {
        // Given
        HabitCreateRequest request = new HabitCreateRequest()
                .name("Drink Water")
                .target(8)
                .frequency(HabitCreateRequest.FrequencyEnum.DAILY);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+jwtToken);
        HttpEntity<HabitCreateRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<HabitPublicResponse> response = restTemplate.postForEntity(
                "/habits",
                entity,
                HabitPublicResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Drink Water");
    }
    @Test
    void shouldGetAllHabitsForUser() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+jwtToken);
        HttpEntity<HabitCreateRequest> entity = new HttpEntity<>(headers);

        ResponseEntity<HabitPublicResponse[]> response =
                restTemplate.exchange("/habits", HttpMethod.GET, entity, HabitPublicResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }
    @Test
    void shouldGetHabitById() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+jwtToken);
        HttpEntity<HabitCreateRequest> entity = new HttpEntity<>(headers);

        ResponseEntity<HabitPublicResponse> response =
                restTemplate.exchange("/habits/" + createdHabitId, HttpMethod.GET, entity, HabitPublicResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(createdHabitId);
    }

    @Test
    void shouldUpdateHabit() {
        HabitCreateRequest update = new HabitCreateRequest()
                .name("Drink More Water")
                .target(10)
                .frequency(HabitCreateRequest.FrequencyEnum.DAILY);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+jwtToken);
        HttpEntity<HabitCreateRequest> entity = new HttpEntity<>(update, headers);

        ResponseEntity<String> response =
                restTemplate.exchange("/habits/" + createdHabitId, HttpMethod.PATCH, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Drink More Water");
    }

    @Test
    void shouldDeleteHabit() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+jwtToken);
        HttpEntity<HabitCreateRequest> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange("/habits/" + createdHabitId, HttpMethod.DELETE, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("deleted");
    }

    @Test
    void shouldGetProgress() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+jwtToken);
        HttpEntity<HabitCreateRequest> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange("/progress?habitId=" + createdHabitId, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("habitId");
    }
}