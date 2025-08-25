package ingsoftware.service;

import ingsoftware.exception.HabitAlreadyCompletedException;
import ingsoftware.exception.HabitNotFoundException;
import ingsoftware.model.DTO.CompletionResultDTO;
import ingsoftware.model.Habit;
import ingsoftware.model.HabitCompletion;
import ingsoftware.model.User;
import ingsoftware.repository.HabitCompletionRepository;
import ingsoftware.repository.HabitRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class HabitCompletionServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCompletionRepository completionRepository;

    @Mock
    private GamificationService gamificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private HabitCompletionService habitCompletionService;

    public HabitCompletionServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCompleteHabit_SuccessfulCompletion() throws Exception {
        Long habitId = 1L;
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setTotalXp(0);

        Habit mockHabit = new Habit();
        mockHabit.setId(habitId);
        //mockHabit.setFrequency(Habit.Frequency.DAILY);
        mockHabit.setCurrentStreak(0);
        mockHabit.setLastCompletedDate(null);

        when(habitRepository.findById(habitId)).thenReturn(Optional.of(mockHabit));
        when(userService.findUserOrThrow(userId)).thenReturn(mockUser);
        when(completionRepository.existsByUserIdAndHabitIdAndCompletionDate(userId, habitId, today)).thenReturn(false);
        when(completionRepository.save(any(HabitCompletion.class))).thenReturn(new HabitCompletion());

        double xpGained = 50.0;
        int newLevel = 2;

        when(gamificationService.calculateHabitXP(mockHabit, mockUser)).thenReturn(xpGained);
        when(gamificationService.checkUpdateUserLevel(mockUser)).thenReturn(newLevel);

        CompletionResultDTO result = habitCompletionService.completeHabit(habitId, userId);

        assertEquals(xpGained, result.getGainedXP());
        assertEquals(newLevel, result.getNewLevelAchieved());
        assertNotNull(result.getCompletion());

        verify(habitRepository).save(mockHabit);
        verify(completionRepository).save(any(HabitCompletion.class));
        verify(userService, times(2)).saveUser(mockUser);
    }

    @Test
    void testCompleteHabit_HabitNotFound() {
        Long habitId = 1L;
        Long userId = 1L;

        when(habitRepository.findById(habitId)).thenReturn(Optional.empty());

        assertThrows(HabitNotFoundException.class, () -> habitCompletionService.completeHabit(habitId, userId));

        verify(habitRepository, never()).save(any(Habit.class));
        verify(completionRepository, never()).save(any(HabitCompletion.class));
    }

    @Test
    void testCompleteHabit_AlreadyCompletedToday() {
        Long habitId = 1L;
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        User mockUser = new User();
        mockUser.setId(userId);

        Habit mockHabit = new Habit();
        mockHabit.setId(habitId);

        when(habitRepository.findById(habitId)).thenReturn(Optional.of(mockHabit));
        when(userService.findUserOrThrow(userId)).thenReturn(mockUser);
        when(completionRepository.existsByUserIdAndHabitIdAndCompletionDate(userId, habitId, today)).thenReturn(true);

        assertThrows(HabitAlreadyCompletedException.class, () -> habitCompletionService.completeHabit(habitId, userId));

        verify(habitRepository, never()).save(mockHabit);
        verify(completionRepository, never()).save(any(HabitCompletion.class));
    }

    @Test
    void testCompleteHabit_FirstCompletionWithNewStreak() throws Exception {
        Long habitId = 1L;
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        User mockUser = new User();
        mockUser.setId(userId);

        Habit mockHabit = new Habit();
        mockHabit.setId(habitId);
        mockHabit.setCurrentStreak(0);
        mockHabit.setLastCompletedDate(null);
        //mockHabit.setFrequency(Habit.Frequency.DAILY);

        when(habitRepository.findById(habitId)).thenReturn(Optional.of(mockHabit));
        when(userService.findUserOrThrow(userId)).thenReturn(mockUser);
        when(completionRepository.existsByUserIdAndHabitIdAndCompletionDate(userId, habitId, today)).thenReturn(false);
        when(completionRepository.save(any(HabitCompletion.class))).thenReturn(new HabitCompletion());

        double xpGained = 30;
        int newLevel = 1;

        when(gamificationService.calculateHabitXP(mockHabit, mockUser)).thenReturn(xpGained);
        when(gamificationService.checkUpdateUserLevel(mockUser)).thenReturn(newLevel);

        CompletionResultDTO result = habitCompletionService.completeHabit(habitId, userId);

        assertNotNull(result.getCompletion());
        assertEquals(1, mockHabit.getCurrentStreak());
        assertEquals(today, mockHabit.getLastCompletedDate());
    }
}