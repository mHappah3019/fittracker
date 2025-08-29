package ingsoftware.service;

import ingsoftware.dao.HabitCompletionDAO;
import ingsoftware.exception.BusinessException;
import ingsoftware.exception.HabitAlreadyCompletedException;
import ingsoftware.model.DTO.CompletionResultDTO;
import ingsoftware.model.Habit;
import ingsoftware.model.HabitCompletion;
import ingsoftware.model.User;
import ingsoftware.model.enum_helpers.HabitDifficulty;
import ingsoftware.model.enum_helpers.HabitFrequencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test per HabitCompletionService - Tests semplici e comprensibili
 * Copre i casi principali del completamento delle abitudini
 */
@ExtendWith(MockitoExtension.class)
class HabitCompletionServiceTest {

    @Mock
    private HabitService habitService;
    
    @Mock
    private HabitCompletionDAO completionDAO;
    
    @Mock
    private GamificationService gamificationService;
    
    @Mock
    private UserService userService;

    @InjectMocks
    private HabitCompletionService habitCompletionService;

    private User testUser;
    private Habit testHabit;
    private HabitCompletion testCompletion;
    private Long userId = 1L;
    private Long habitId = 1L;
    
    @BeforeEach
    void setUp() {
        // Crea utente di test
        testUser = new User();
        testUser.setId(userId);
        testUser.setLevel(2);
        testUser.setTotalXp(150.0);

        // Crea abitudine di test
        testHabit = new Habit();
        testHabit.setId(habitId);
        testHabit.setName("Test Habit");
        testHabit.setDifficulty(HabitDifficulty.MEDIUM);
        testHabit.setFrequency(HabitFrequencyType.DAILY);
        testHabit.setUserId(userId);
        testHabit.setCurrentStreak(5);
        testHabit.setLastCompletedDate(LocalDate.now().minusDays(1));

        // Crea completamento di test
        testCompletion = new HabitCompletion();
        testCompletion.setId(1L);
        testCompletion.setUserId(userId);
        testCompletion.setHabitId(habitId);
        testCompletion.setCompletionDate(LocalDate.now());
        testCompletion.setStreak(6);
    }

    // ===========================================
    // TEST PER completeHabit() - Casi di Errore
    // ===========================================

    @Test
    void testCompleteHabit_HabitNotFound_ThrowsException() {
        // Arrange
        when(habitService.findHabitOrThrow(habitId)).thenThrow(new RuntimeException("Habit not found"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> habitCompletionService.completeHabit(habitId, userId),
                "Dovrebbe lanciare eccezione quando l'abitudine non esiste");

        // Verifica che non sia stato salvato nulla
        verify(completionDAO, never()).save(any(HabitCompletion.class));
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void testCompleteHabit_AlreadyCompletedToday_ThrowsException() {
        // Arrange
        when(habitService.findHabitOrThrow(habitId)).thenReturn(testHabit);
        when(userService.findUserOrThrow(userId)).thenReturn(testUser);
        when(completionDAO.existsByUserIdAndHabitIdAndCompletionDate(eq(userId), eq(habitId), any(LocalDate.class)))
            .thenReturn(true);

        // Act & Assert
        HabitAlreadyCompletedException exception = assertThrows(HabitAlreadyCompletedException.class,
                () -> habitCompletionService.completeHabit(habitId, userId),
                "Dovrebbe lanciare HabitAlreadyCompletedException quando già completata oggi");
        
        assertTrue(exception.getMessage().contains("già completata"), 
                  "Il messaggio dovrebbe indicare che l'abitudine è già stata completata");
        
        // Verifica che non sia stato salvato nulla
        verify(completionDAO, never()).save(any(HabitCompletion.class));
    }

    // ===========================================
    // TEST PER updateStreak() - attraverso completeHabit()
    // ===========================================

    @Test
    void testCompleteHabit_FirstCompletion_SetsStreakToOne() throws BusinessException {
        // Arrange: prima volta che viene completata
        testHabit.setLastCompletedDate(null);
        testHabit.setCurrentStreak(0);
        
        when(habitService.findHabitOrThrow(habitId)).thenReturn(testHabit);
        when(userService.findUserOrThrow(userId)).thenReturn(testUser);
        when(completionDAO.existsByUserIdAndHabitIdAndCompletionDate(any(), any(), any())).thenReturn(false);
        when(completionDAO.save(any(HabitCompletion.class))).thenReturn(testCompletion);
        when(gamificationService.calculateHabitXP(any(), any())).thenReturn(20.0);
        when(gamificationService.checkUpdateUserLevel(any())).thenReturn(0);

        // Act
        habitCompletionService.completeHabit(habitId, userId);

        // Assert: verifica che l'abitudine sia stata aggiornata
        verify(habitService).saveHabit(argThat(habit -> 
            habit.getCurrentStreak() == 1 && habit.getLastCompletedDate().equals(LocalDate.now())
        ));
    }

    @Test
    void testCompleteHabit_ConsecutiveDay_IncrementsStreak() throws BusinessException {
        // Arrange: completamento consecutivo (ieri e oggi)
        testHabit.setLastCompletedDate(LocalDate.now().minusDays(1));
        testHabit.setCurrentStreak(3);
        
        when(habitService.findHabitOrThrow(habitId)).thenReturn(testHabit);
        when(userService.findUserOrThrow(userId)).thenReturn(testUser);
        when(completionDAO.existsByUserIdAndHabitIdAndCompletionDate(any(), any(), any())).thenReturn(false);
        when(completionDAO.save(any(HabitCompletion.class))).thenReturn(testCompletion);
        when(gamificationService.calculateHabitXP(any(), any())).thenReturn(20.0);
        when(gamificationService.checkUpdateUserLevel(any())).thenReturn(0);

        // Act
        habitCompletionService.completeHabit(habitId, userId);

        // Assert: verifica che il streak sia incrementato
        verify(habitService).saveHabit(argThat(habit -> habit.getCurrentStreak() == 4));
    }

    @Test
    void testCompleteHabit_SkippedDay_ResetsStreakToOne() throws BusinessException {
        // Arrange: completamento dopo aver saltato un giorno
        testHabit.setLastCompletedDate(LocalDate.now().minusDays(3)); // 3 giorni fa
        testHabit.setCurrentStreak(5);
        
        when(habitService.findHabitOrThrow(habitId)).thenReturn(testHabit);
        when(userService.findUserOrThrow(userId)).thenReturn(testUser);
        when(completionDAO.existsByUserIdAndHabitIdAndCompletionDate(any(), any(), any())).thenReturn(false);
        when(completionDAO.save(any(HabitCompletion.class))).thenReturn(testCompletion);
        when(gamificationService.calculateHabitXP(any(), any())).thenReturn(20.0);
        when(gamificationService.checkUpdateUserLevel(any())).thenReturn(0);

        // Act
        habitCompletionService.completeHabit(habitId, userId);

        // Assert: verifica che il streak sia azzerato e riparta da 1
        verify(habitService).saveHabit(argThat(habit -> habit.getCurrentStreak() == 1));
    }
}