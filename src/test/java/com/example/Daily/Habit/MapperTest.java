//package com.example.Daily.Habit;
//
//import com.example.mapper.HabitMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class MapperTest {
//
//    @Autowired
//    private HabitMapper habitMapper;
//
//    @Test
//    public void testMappersAreAutowired() {
//        assertNotNull(habitMapper, "HabitMapper should be autowired");
//    }
//
//    @Test
//    public void testUserMapping() {
//        // Test user mapping directly without mapper
//        com.example.Daily.Habit.entity.User userEntity = new com.example.Daily.Habit.entity.User();
//        userEntity.setUserId(java.util.UUID.randomUUID());
//        userEntity.setName("Test User");
//        userEntity.setEmail("test@example.com");
//        userEntity.setPassword("password");
//        userEntity.setRole(com.example.Daily.Habit.entity.Role.USER);
//
//        // Test that the entity can be created
//        assertNotNull(userEntity);
//        assertEquals("Test User", userEntity.getName());
//        assertEquals("test@example.com", userEntity.getEmail());
//    }
//
//    @Test
//    public void testHabitMapping() {
//        // Test habit mapping
//        com.example.Daily.Habit.entity.Habit habitEntity = new com.example.Daily.Habit.entity.Habit();
//        habitEntity.setId(java.util.UUID.randomUUID());
//        habitEntity.setName("Test Habit");
//        habitEntity.setFrequency(com.example.Daily.Habit.entity.Frequency.DAILY);
//        habitEntity.setTarget(10);
//        habitEntity.setProgress(5.0);
//
//        com.example.Daily.Habit.model.Habit habitDto = habitMapper.toDto(habitEntity);
//
//        assertNotNull(habitDto);
//        assertEquals(habitEntity.getId(), habitDto.getId());
//        assertEquals(habitEntity.getName(), habitDto.getName());
//        assertEquals(habitEntity.getTarget(), habitDto.getTarget());
//    }
//}
