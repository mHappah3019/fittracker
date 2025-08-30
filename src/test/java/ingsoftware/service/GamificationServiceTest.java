package ingsoftware.service;

import ingsoftware.model.*;
import ingsoftware.model.DTO.LifePointsDTO;
import ingsoftware.model.enum_helpers.HabitDifficulty;
import ingsoftware.service.strategy.ExperienceStrategyFactory;
import ingsoftware.service.strategy.GamificationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

/**
 * Test per GamificationService - Tests semplici e comprensibili
 * Copre i casi principali del sistema di gamificazione
 */
@ExtendWith(MockitoExtension.class)
class GamificationServiceTest {

    @Mock
    private HabitService habitService;
    
    @Mock
    private LifePointCalculator calculator;
    
    @Mock
    private ExperienceStrategyFactory strategyFactory;
    
    @Mock
    private GamificationStrategy gamificationStrategy;

    @InjectMocks
    private GamificationService gamificationService;

    private User testUser;
    private Habit easyHabit;
    private Habit hardHabit;
    
    @BeforeEach
    void setUp() {
        // Crea un utente di test
        testUser = new User();
        testUser.setId(1L);
        testUser.setLevel(2);
        testUser.setTotalXp(120.0);
        testUser.setLifePoints(75);
        testUser.setLastAccessDate(LocalDate.of(2024, 1, 10));

        // Crea abitudini di test con diverse difficoltà
        easyHabit = createTestHabit("Bere Acqua", HabitDifficulty.EASY);
        hardHabit = createTestHabit("Correre 5km", HabitDifficulty.HARD);
    }

    private Habit createTestHabit(String name, HabitDifficulty difficulty) {
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName(name);
        habit.setDifficulty(difficulty);
        habit.setUserId(testUser.getId());
        habit.setLastCompletedDate(LocalDate.of(2024, 1, 10));
        return habit;
    }

    // ===========================================
    // TEST PER calculateHabitXP()
    // ===========================================

    @Test
    void testCalculateHabitXP_EasyHabit_ReturnsCorrectXP() {
        // Arrange
        when(strategyFactory.createStrategy()).thenReturn(gamificationStrategy);
        when(gamificationStrategy.calculateExperience(anyDouble(), any(User.class))).thenReturn(15.0);

        // Act
        double result = gamificationService.calculateHabitXP(easyHabit, testUser);

        // Assert
        assertEquals(15.0, result, "Dovrebbe restituire l'XP calcolato dalla strategia");
        verify(strategyFactory).createStrategy();
        verify(gamificationStrategy).calculateExperience(easyHabit.getDifficulty().getBaseXP(), testUser);
    }

    @Test
    void testCalculateHabitXP_HardHabit_ReturnsCorrectXP() {
        // Arrange
        when(strategyFactory.createStrategy()).thenReturn(gamificationStrategy);
        when(gamificationStrategy.calculateExperience(anyDouble(), any(User.class))).thenReturn(50.0);

        // Act
        double result = gamificationService.calculateHabitXP(hardHabit, testUser);

        // Assert
        assertEquals(50.0, result, "Le abitudini difficili dovrebbero dare più XP");
        verify(gamificationStrategy).calculateExperience(hardHabit.getDifficulty().getBaseXP(), testUser);
    }

    // ===========================================
    // TEST PER updateUserLifePoints()
    // ===========================================

    @Test
    void testUpdateUserLifePoints_CompletedHabitsYesterday_GainPoints() {
        // Arrange: abitudini completate ieri
        LocalDate yesterday = LocalDate.of(2024, 1, 14);
        LocalDate today = LocalDate.of(2024, 1, 15);
        testUser.setLastAccessDate(yesterday);

        easyHabit.setLastCompletedDate(yesterday);
        hardHabit.setLastCompletedDate(yesterday);
        
        List<Habit> completedHabits = Arrays.asList(easyHabit, hardHabit);
        when(habitService.findAllByUserId(testUser.getId())).thenReturn(completedHabits);
        when(calculator.compute(anyList(), anyList())).thenReturn(30);

        // Act
        LifePointsDTO result = gamificationService.updateUserLifePoints(testUser, today);

        // Assert
        assertEquals(75, result.getOldLifePoints(), "I punti vita iniziali dovrebbero essere 75");
        assertTrue(result.getNewLifePoints() > 75, "I punti vita dovrebbero aumentare");
        assertFalse(result.isLevelDecreased(), "Il livello non dovrebbe diminuire");
        verify(calculator).compute(anyList(), anyList());
    }

    @Test
    void testUpdateUserLifePoints_InactivityPenalty_LosePoints() {
        // Arrange: 3 giorni di inattività
        LocalDate threeDaysAgo = LocalDate.of(2024, 1, 12);
        LocalDate today = LocalDate.of(2024, 1, 15);
        testUser.setLastAccessDate(threeDaysAgo);
        
        when(habitService.findAllByUserId(testUser.getId())).thenReturn(Arrays.asList(easyHabit));

        // Act
        LifePointsDTO result = gamificationService.updateUserLifePoints(testUser, today);

        // Assert
        assertEquals(75, result.getOldLifePoints(), "I punti vita iniziali dovrebbero essere 75");
        assertTrue(result.getNewLifePoints() < 75, "I punti vita dovrebbero diminuire per inattività");
        
        // Verifica che sia stata applicata la penalità per inattività
        // 2 giorni di inattività * -10 punti = -20 punti + penalità base
        int expectedPenalty = 2 * gamificationService.INACTIVITY_PENALTY_PER_DAY; // -20
        assertTrue(result.getNewLifePoints() <= 75 + expectedPenalty, 
                  "Dovrebbe applicare la penalità per inattività");
    }

    // ===========================================
    // TEST PER checkUpdateUserLevel()
    // ===========================================

    @Test
    void testCheckUpdateUserLevel_EnoughXPForLevelUp_UpdatesLevel() {
        // Arrange: XP sufficiente per salire al livello 3 (200+ XP)
        testUser.setTotalXp(250.0);
        testUser.setLevel(2);

        // Act
        int newLevel = gamificationService.checkUpdateUserLevel(testUser);

        // Assert
        assertEquals(3, newLevel, "Dovrebbe salire al livello 3");
        assertEquals(3, testUser.getLevel(), "Il livello dell'utente dovrebbe essere aggiornato");
    }

    @Test
    void testCheckUpdateUserLevel_NotEnoughXP_NoLevelChange() {
        // Arrange: XP non sufficiente per salire di livello
        testUser.setTotalXp(150.0); // Livello 2 richiede 100-199 XP
        testUser.setLevel(2);

        // Act
        int newLevel = gamificationService.checkUpdateUserLevel(testUser);

        // Assert
        assertEquals(0, newLevel, "Non dovrebbe esserci cambio di livello");
        assertEquals(2, testUser.getLevel(), "Il livello dovrebbe rimanere 2");
    }

    // ===========================================
    // TEST PER checkAndHandleLifePointsDepletion()
    // ===========================================

    @Test
    void testCheckAndHandleLifePointsDepletion_NegativeLifePoints_DecreasesLevel() {
        // Arrange: punti vita minori di 1, livello maggiore di 1
        testUser.setLifePoints(-10);
        testUser.setLevel(2);

        // Act
        boolean result = gamificationService.checkAndHandleLevelDecrease(testUser);

        // Assert
        assertTrue(result, "Dovrebbe restituire true quando il livello diminuisce");
        assertEquals(1, testUser.getLevel(), "Il livello dovrebbe diminuire a 1");
        assertEquals(50, testUser.getLifePoints(), "I punti vita dovrebbero essere ripristinati a 50");
    }

    @Test
    void testCheckAndHandleLifePointsDepletion_Level1WithZeroLifePoints_NoChange() {
        // Arrange: punti vita minori di 1, ma livello già 1
        testUser.setLifePoints(0);
        testUser.setLevel(1);

        // Act
        boolean result = gamificationService.checkAndHandleLevelDecrease(testUser);

        // Assert
        assertFalse(result, "Non dovrebbe diminuire il livello se già al livello 1");
        assertEquals(1, testUser.getLevel(), "Il livello dovrebbe rimanere 1");
        assertEquals(0, testUser.getLifePoints(), "I punti vita dovrebbero rimanere 0");
    }


}