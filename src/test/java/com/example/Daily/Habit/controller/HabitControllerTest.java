package com.example.Daily.Habit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.Daily.Habit.Application;
import com.example.Daily.Habit.entity.Role;
import com.example.Daily.Habit.repository.HabitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.Daily.Habit.entity.Frequency;
import com.example.Daily.Habit.entity.Habit;
import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.mapper.HabitMapper;
import com.example.Daily.Habit.model.HabitCreateRequest;
import com.example.Daily.Habit.model.HabitDetailsResponse;
import com.example.Daily.Habit.model.HabitPublicResponse;
import com.example.Daily.Habit.model.HabitUpdateRequest;
import com.example.Daily.Habit.model.HabitUpdateResponse;
import com.example.Daily.Habit.model.MessageResponse;
import com.example.Daily.Habit.service.HabitService;
import com.example.Daily.Habit.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class HabitControllerTest {

    @Mock private HabitService habitService;
    @Mock private UserService userService;
    @Mock private HabitMapper habitMapper;

    @InjectMocks private HabitController habitController;

    private static final UUID userId = UUID.randomUUID();
    private static final String habitId = UUID.randomUUID().toString();

    private User user;
    private Habit habit;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(userId);
        final String email = "user@gmail.com";


        habit = new Habit();
        habit.setId(UUID.fromString(habitId));
        habit.setName("Drink Water");
        habit.setTarget(8);
        habit.setFrequency(Frequency.DAILY);
        habit.setUser(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        when(userService.findByEmail(email)).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateHabitForUser() {
         // Given
        HabitCreateRequest request = new HabitCreateRequest().name("Drink Water");
        HabitPublicResponse response = new HabitPublicResponse().name("Drink Water");

        when(habitMapper.toEntity(request)).thenReturn(habit);
        when(habitService.save(habit)).thenReturn(habit);
        when(habitMapper.toPublicResponse(habit)).thenReturn(response);

         // When
        ResponseEntity<HabitPublicResponse> result = habitController.createHabit(request);

        // Then
        assertEquals(201, result.getStatusCodeValue());
        assertEquals("Drink Water", result.getBody().getName());
        verify(habitService, times(1)).save(habit);
    }

    
    @Test
    void shouldReturnHabitDetails_whenHabitBelongsToUser() {
        // Given
        HabitDetailsResponse details = new HabitDetailsResponse().name("Drink Water");

        when(habitService.findById(habitId)).thenReturn(habit);
        when(habitMapper.toDetailsResponse(habit)).thenReturn(details);

        // When
        ResponseEntity<HabitDetailsResponse> result = habitController.getHabitById(habitId.toString());

        // Then
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Drink Water", result.getBody().getName());
    }

    @Test
    void shouldReturnForbidden_whenHabitDoesNotBelongToUser() {
        // Given
        Habit otherUserHabit = new Habit();
        otherUserHabit.setId(UUID.fromString(habitId));
        User other = new User();
        other.setUserId(UUID.randomUUID());
        otherUserHabit.setUser(other);

        when(habitService.findById(habitId)).thenReturn(otherUserHabit);

        // When
        ResponseEntity<HabitDetailsResponse> result = habitController.getHabitById(habitId.toString());

        // Then
        assertEquals(403, result.getStatusCodeValue());
        assertNull(result.getBody());
    }

    @Test
    void shouldReturnAllHabitsForUser() {
        // Given
        HabitPublicResponse dto = new HabitPublicResponse().name("Drink Water");

        when(habitService.getHabitsByUser(userId)).thenReturn(List.of(habit));
        when(habitMapper.toPublicResponse(habit)).thenReturn(dto);
        // When
        ResponseEntity<List<HabitPublicResponse>> result = habitController.getHabits();
        // Then
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().size());
        assertEquals("Drink Water", result.getBody().get(0).getName());
    }

    @Test
    void shouldUpdateHabit_whenHabitBelongsToUser() {
        // Given
        HabitUpdateRequest updateRequest = new HabitUpdateRequest().name("Updated Habit");

        when(habitService.findById(habitId)).thenReturn(habit);
        doAnswer(invocation -> {
            habit.setName("Updated Habit");
            return null;
        }).when(habitMapper).updateEntityFromRequest(updateRequest, habit);
        when(habitService.save(habit)).thenReturn(habit);
        // When
        ResponseEntity<HabitUpdateResponse> result = habitController.updateHabit(habitId.toString(), updateRequest);
        // Then
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Updated Habit", result.getBody().getName());
    }

    @Test
    void shouldDeleteHabit_whenHabitBelongsToUser() {
        // Given
        when(habitService.findById(habitId)).thenReturn(habit);
        // When
        ResponseEntity<MessageResponse> result = habitController.deleteHabit(habitId);
        // Then
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Habit deleted successfully", result.getBody().getMessage());
        verify(habitService, times(1)).delete(habitId);
    }

    @Test
    void shouldReturnForbidden_whenDeletingHabitNotBelongingToUser() {
        Habit otherHabit = new Habit();
        otherHabit.setId(UUID.fromString(habitId));
        User otherUser = new User();
        otherUser.setUserId(UUID.randomUUID());
        otherHabit.setUser(otherUser);

        when(habitService.findById(habitId)).thenReturn(otherHabit);

        ResponseEntity<MessageResponse> result = habitController.deleteHabit(habitId.toString());

        assertEquals(403, result.getStatusCodeValue());
    }

    @SpringBootTest(classes = Application.class)
    @AutoConfigureMockMvc
    static
    class HabitControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private HabitRepository habitRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private com.example.Daily.Habit.repository.UserRepository userRepository;


        private User testUser;

        @BeforeEach
        void setup() {
            habitRepository.deleteAll();
            userRepository.deleteAll();

            // Insert test user
            testUser = new User();
            //testUser.setHabits(UUID.randomUUID().toString());
            testUser.setEmail("test@example.com");
            testUser.setName("Test User");
            testUser.setPassword("password");
            testUser.setRole(Role.USER);
            testUser = userRepository.save(testUser);

            System.out.println("✅ Setup completed, inserted user with ID: " + testUser.getUserId());
        }

        @Test
        void shouldCreateHabitSuccessfully() throws Exception {
            String jsonBody = "{ \"name\": \"Drink Water\", \"frequency\": \"DAILY\" }";

            System.out.println("➡ Sending POST /habit request with body: " + jsonBody);

            String response = mockMvc.perform(post("/habit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            System.out.println("⬅ Response from POST /habit: " + response);
    //        User user = new User();
    //        user.setName("testuser");
    //        user.setRole(Role.USER);
    //        user.setEmail("testuser@example.com");
    //        user.setPassword("password");
    //        user = userRepository.save(user);
    //
    //        Habit habit = new Habit();
    //        habit.setName("Drink Water");
    //        habit.setFrequency(Frequency.DAILY);
    //        habit.setTarget(8);
    //        habit.setProgress(0.00);
    //        habit.setUser(user);
    //        habit = habitRepository.save(habit);
    //        mockMvc.perform(post("/user/" + user.getUserId() + "/habit")
    //                .contentType(MediaType.APPLICATION_JSON)
    //                .content(objectMapper.writeValueAsString(habit)))
    //                .andExpect(status().isCreated())
    //                .andExpect(jsonPath("$.name").value("Drink Water"));
        }

        @Test
        void shouldNotCreateHabitForNonExistingUser() throws Exception {
           User user = new User();
            user.setName("testuser");
            user.setRole(Role.USER);
            user.setEmail("testuser@example.com");
            user.setPassword("password");
            user = userRepository.save(user);

            Habit habit = new Habit();
            habit.setName("Drink Water");
            habit.setFrequency(Frequency.DAILY);
            habit.setTarget(8);
            habit.setProgress(0.00);
            habit.setUser(user);
            habit = habitRepository.save(habit);
            // TODO: Must handle any ID sent
            // if got any ID rather than a UUID formate it gives 500 status code (VERY WEIRD)
            mockMvc.perform(post("/user/3fa85f64-5717-4562-b3fc-2c963f66afa6/habit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(habit)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldGetAllHabitsForUser() throws Exception {
            User user = new User();
            user.setName("testuser");
            user.setRole(Role.USER);
            user.setEmail("testuser@example.com");
            user.setPassword("password");
            user = userRepository.save(user);

            Habit habit = new Habit();
            habit.setName("Drink Water");
            habit.setFrequency(Frequency.DAILY);
            habit.setTarget(8);
            habit.setProgress(0.00);
            habit.setUser(user);
            habit = habitRepository.save(habit);

            Habit habit2 = new Habit();
            habit2.setName("Read Book");
            habit2.setFrequency(Frequency.WEEKLY);
            habit2.setTarget(1);
            habit2.setProgress(0.00);
            habit2.setUser(user);
            habit2 = habitRepository.save(habit2);

            mockMvc.perform(get("/user/" + user.getUserId() + "/habit"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void shouldReturnEmptyListForUserWithNoHabits() throws Exception {
           User user = new User();
            user.setName("testuser");
            user.setRole(Role.USER);
            user.setEmail("testuser@example.com");
            user.setPassword("password");
            user = userRepository.save(user);


            mockMvc.perform(get("/user/" + user.getUserId() + "/habit"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void shouldDeleteHabitById() throws Exception {
             User user = new User();
            user.setName("testuser");
            user.setRole(Role.USER);
            user.setEmail("testuser@example.com");
            user.setPassword("password");
            user = userRepository.save(user);

            Habit habit = new Habit();
            habit.setName("Drink Water");
            habit.setFrequency(Frequency.DAILY);
            habit.setTarget(8);
            habit.setProgress(0.00);
            habit.setUser(user);
            habit = habitRepository.save(habit);

            mockMvc.perform(delete("/user/" + user.getUserId() + "/habit/" + habit.getId()))
                    .andExpect(status().isOk());

            List<Habit> allHabits = habitRepository.findAll();
            org.assertj.core.api.Assertions.assertThat(allHabits).isEmpty();
        }
        @Test
        void shouldNotDeleteExistingHabitThatDoesNotBelongToUser() throws Exception {
            User user = new User();
            user.setName("testuser");
            user.setRole(Role.USER);
            user.setEmail("testuser@example.com");
            user.setPassword("password");
            user = userRepository.save(user);

            User anotherUser = new User();
            anotherUser.setName("anotheruser");
            anotherUser.setRole(Role.USER);
            anotherUser.setEmail("anotheruser@example.com");
            anotherUser.setPassword("password");
            anotherUser = userRepository.save(anotherUser);

            Habit habit = new Habit();
            habit.setName("Drink Water");
            habit.setFrequency(Frequency.DAILY);
            habit.setTarget(8);
            habit.setProgress(0.00);
            habit.setUser(anotherUser);
            habit = habitRepository.save(habit);
           mockMvc.perform(delete("/user/" + user.getUserId() + "/habit/" + habit.getId()))
                    .andExpect(status().isForbidden());

            List<Habit> allHabits = habitRepository.findAll();
            org.assertj.core.api.Assertions.assertThat(allHabits).hasSize(1);
        }
    }
}