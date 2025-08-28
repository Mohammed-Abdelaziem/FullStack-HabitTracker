package com.example.Daily.Habit.service;

import com.example.Daily.Habit.entity.Frequency;
import com.example.Daily.Habit.entity.Habit;
import com.example.Daily.Habit.entity.OngoingHabit;
import com.example.Daily.Habit.entity.OngoingHabitStatus;
import com.example.Daily.Habit.entity.User;
import com.example.Daily.Habit.errorhandler.AppException;
import com.example.Daily.Habit.repository.HabitRepository;
import com.example.Daily.Habit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock private HabitRepository habitRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private HabitService habitService;

    private static final UUID HABIT_ID = UUID.randomUUID();
    private static final UUID USER_ID  = UUID.randomUUID();

    private User user;
    private Habit habit;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(USER_ID);

        habit = new Habit();
        habit.setId(HABIT_ID);
        habit.setName("Drink Water");
        habit.setTarget(8);
        habit.setFrequency(Frequency.DAILY);
        habit.setProgress(0.0);
        habit.setUser(user);
        habit.setOngoingHabits(new ArrayList<>());
    }

    // ===== findById =====
    @Test
    void findById_found_returnsHabit() {
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));

        Habit res = habitService.findById(HABIT_ID);

        assertNotNull(res);
        assertEquals(HABIT_ID, res.getId());
        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void findById_notFound_throwsNotFound() {
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> habitService.findById(HABIT_ID));
        verify(habitRepository).findById(HABIT_ID);
    }

    // ===== save =====
    @Test
    void save_validUser_savesAndReturns() {
        when(habitRepository.save(habit)).thenReturn(habit);

        Habit saved = habitService.save(habit);

        assertEquals(HABIT_ID, saved.getId());
        verify(habitRepository).save(habit);
    }

    @Test
    void save_nullUser_throwsNotFound() {
        Habit h = new Habit();
        h.setUser(null);

        AppException ex = assertThrows(AppException.class, () -> habitService.save(h));
        assertTrue(ex.getMessage().contains("Habit must be linked"));
        verify(habitRepository, never()).save(any());
    }


    // ===== delete =====
    @Test
    void delete_existing_deletes() {
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));

        habitService.delete(HABIT_ID);

        verify(habitRepository).findById(HABIT_ID);
        verify(habitRepository).delete(habit);
    }

    @Test
    void delete_notFound_throwsNotFound() {
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> habitService.delete(HABIT_ID));
        verify(habitRepository).findById(HABIT_ID);
        verify(habitRepository, never()).delete(any());
    }

    // ===== findAll (no paging) =====
    @Test
    void findAll_returnsCollection() {
        when(habitRepository.findAll()).thenReturn(List.of(habit));

        Collection<Habit> all = habitService.findAll();

        assertEquals(1, all.size());
        verify(habitRepository).findAll();
    }

    // ===== findAll (paging) =====
    @Test
    void findAll_withPaging_returnsPage() {
        PageRequest pr = PageRequest.of(0, 5);
        Page<Habit> page = new PageImpl<>(List.of(habit), pr, 1);
        when(habitRepository.findAll(pr)).thenReturn(page);

        Page<Habit> result = habitService.findAll(0, 5);

        assertEquals(1, result.getTotalElements());
        verify(habitRepository).findAll(pr);
    }

    // ===== getHabitsByUser =====
    @Test
    void getHabitsByUser_userFound_returnsList() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(habitRepository.findByUser(user)).thenReturn(List.of(habit));

        List<Habit> res = habitService.getHabitsByUser(USER_ID);

        assertEquals(1, res.size());
        assertEquals(HABIT_ID, res.get(0).getId());
        verify(userRepository).findById(USER_ID);
        verify(habitRepository).findByUser(user);
    }

    @Test
    void getHabitsByUser_userMissing_throwsNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> habitService.getHabitsByUser(USER_ID));
        verify(userRepository).findById(USER_ID);
        verify(habitRepository, never()).findByUser(any());
    }

    // ===== countCompletedHabits =====
    @Test
    void countCompletedHabits_userFound_returnsCount() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(habitRepository.countByUserAndProgressGreaterThan(user, 0.0)).thenReturn(3L);

        long cnt = habitService.countCompletedHabits(USER_ID);

        assertEquals(3L, cnt);
        verify(userRepository).findById(USER_ID);
        verify(habitRepository).countByUserAndProgressGreaterThan(user, 0.0);
    }

    @Test
    void countCompletedHabits_userMissing_throwsIllegalArgument() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> habitService.countCompletedHabits(USER_ID));
        verify(habitRepository, never()).countByUserAndProgressGreaterThan(any(), anyDouble());
    }

    // ===== countHabitsByUser =====
    @Test
    void countHabitsByUser_userFound_returnsCount() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(habitRepository.countByUser(user)).thenReturn(5L);

        long cnt = habitService.countHabitsByUser(USER_ID);

        assertEquals(5L, cnt);
        verify(userRepository).findById(USER_ID);
        verify(habitRepository).countByUser(user);
    }

    @Test
    void countHabitsByUser_userMissing_throwsIllegalArgument() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> habitService.countHabitsByUser(USER_ID));
        verify(habitRepository, never()).countByUser(any());
    }

    // ===== hasOngoingHabitsWithActiveStatus =====
    @Test
    void hasOngoingHabitsWithActiveStatus_trueWhenStartedOrInProgress() {
        OngoingHabit oh1 = new OngoingHabit();
        oh1.setStatus(OngoingHabitStatus.STARTED);
        habit.getOngoingHabits().add(oh1);

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));

        assertTrue(habitService.hasOngoingHabitsWithActiveStatus(HABIT_ID));
        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void hasOngoingHabitsWithActiveStatus_falseWhenNoActive() {
        OngoingHabit oh1 = new OngoingHabit();
        oh1.setStatus(OngoingHabitStatus.COMPLETED);
        habit.getOngoingHabits().add(oh1);

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));

        assertFalse(habitService.hasOngoingHabitsWithActiveStatus(HABIT_ID));
        verify(habitRepository).findById(HABIT_ID);
    }

    @Test
    void hasOngoingHabitsWithActiveStatus_notFoundHabit_throwsNotFound() {
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> habitService.hasOngoingHabitsWithActiveStatus(HABIT_ID));
    }

    // ===== updateHabit =====
    @Test
    void updateHabit_success_updatesAllowedFieldsAndSaves() {

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));
        when(habitRepository.save(any(Habit.class))).thenAnswer(inv -> inv.getArgument(0));

        Habit updated = habitService.updateHabit(HABIT_ID, "New Name", 12, "WEEKLY");

        assertEquals("New Name", updated.getName());
        assertEquals(12, updated.getTarget());
        assertEquals(Frequency.WEEKLY, updated.getFrequency());

        InOrder inOrder = inOrder(habitRepository);
        inOrder.verify(habitRepository, times(2)).findById(HABIT_ID);
        verify(habitRepository).save(habit);
    }

    @Test
    void updateHabit_partialUpdate_nullsAreIgnored() {
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));
        when(habitRepository.save(any(Habit.class))).thenAnswer(inv -> inv.getArgument(0));


        Habit updated = habitService.updateHabit(HABIT_ID, "Only Name", null, null);

        assertEquals("Only Name", updated.getName());
        assertEquals(8, updated.getTarget());
        assertEquals(Frequency.DAILY, updated.getFrequency());
        verify(habitRepository).save(habit);
    }

    @Test
    void updateHabit_activeOngoing_throwsIllegalState() {

        OngoingHabit oh = new OngoingHabit();
        oh.setStatus(OngoingHabitStatus.STARTED);
        habit.getOngoingHabits().add(oh);

        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));

        assertThrows(IllegalStateException.class,
                () -> habitService.updateHabit(HABIT_ID, "X", 10, "DAILY"));

        verify(habitRepository, never()).save(any());
    }

    @Test
    void updateHabit_invalidFrequencyString_throwsIllegalArgument() {
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.of(habit));

        assertThrows(IllegalArgumentException.class,
                () -> habitService.updateHabit(HABIT_ID, null, null, "INVALID_FREQUENCY"));

        verify(habitRepository, never()).save(any());
    }

    @Test
    void updateHabit_habitNotFound_throwsNotFound() {
        when(habitRepository.findById(HABIT_ID)).thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> habitService.updateHabit(HABIT_ID, "n", 1, "DAILY"));
        verify(habitRepository, never()).save(any());
    }
}
