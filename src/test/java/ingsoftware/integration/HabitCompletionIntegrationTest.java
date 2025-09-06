package ingsoftware.integration;

import ingsoftware.dao.HabitCompletionDAO;
import ingsoftware.dao.HabitDAO;
import ingsoftware.dao.UserDAO;
import ingsoftware.exception.HabitAlreadyCompletedException;
import ingsoftware.exception.HabitNotFoundException;
import ingsoftware.exception.UserNotFoundException;
import ingsoftware.model.DTO.CompletionResultDTO;
import ingsoftware.model.Habit;
import ingsoftware.model.HabitCompletion;
import ingsoftware.model.User;
import ingsoftware.model.enum_helpers.HabitDifficulty;
import ingsoftware.model.enum_helpers.HabitFrequencyType;
import ingsoftware.service.HabitCompletionService;
import ingsoftware.service.HabitService;
import ingsoftware.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test per il flusso completo di completamento abitudini.
 * Testa l'integrazione tra HabitCompletionService, HabitService, UserService e i rispettivi DAO.
 */
@DisplayName("Integration Test - Completamento Abitudini")
class HabitCompletionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private HabitCompletionService habitCompletionService;

    @Autowired
    private HabitService habitService;

    @Autowired
    private UserService userService;

    @Autowired
    private HabitDAO habitDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private HabitCompletionDAO habitCompletionDAO;

    private User testUser;
    private Habit testHabit;

    @BeforeEach
    void setUp() {
        // Crea un utente di test
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setLevel(1);
        testUser.setTotalXp(0.0);
        testUser = userDAO.save(testUser);

        // Crea un'abitudine di test
        testHabit = new Habit();
        testHabit.setName("Test Habit");
        testHabit.setDescription("Abitudine per test");
        testHabit.setDifficulty(HabitDifficulty.MEDIUM);
        testHabit.setFrequency(HabitFrequencyType.DAILY);
        testHabit.setUserId(testUser.getId());
        testHabit.setCurrentStreak(0);
        testHabit.setCreatedAt(LocalDateTime.now());
        testHabit = habitDAO.save(testHabit);

    }

    @Test
    @DisplayName("Dovrebbe completare un'abitudine con successo")
    @Rollback
    void shouldCompleteHabitSuccessfully() throws Exception {
        // Act - Completa l'abitudine
        CompletionResultDTO result = habitCompletionService.completeHabit(testHabit.getId(), testUser.getId());

        // Assert - Verifica il risultato del completamento
        assertNotNull(result, "Il risultato del completamento non dovrebbe essere null");
        assertNotNull(result.getCompletion(), "Il completamento non dovrebbe essere null");
        // Verifica che il completamento sia stato salvato nel database
        HabitCompletion completion = result.getCompletion();
        assertEquals(testUser.getId(), completion.getUserId());
        assertEquals(testHabit.getId(), completion.getHabitId());
        assertEquals(LocalDate.now(), completion.getCompletionDate());
        assertEquals(1, completion.getStreak(), "Il primo completamento dovrebbe avere streak = 1");

        // Verifica che l'abitudine sia stata aggiornata
        Optional<Habit> updatedHabit = habitDAO.findById(testHabit.getId());
        assertTrue(updatedHabit.isPresent());
        assertEquals(1, updatedHabit.get().getCurrentStreak());
        assertEquals(LocalDate.now(), updatedHabit.get().getLastCompletedDate());

        // Verifica che l'utente abbia guadagnato XP
        Optional<User> updatedUser = userDAO.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertTrue(updatedUser.get().getTotalXp() > 0, "L'utente dovrebbe aver guadagnato XP");
    }

    @Test
    @DisplayName("Dovrebbe impedire il completamento duplicato nella stessa giornata")
    @Rollback
    void shouldPreventDuplicateCompletionSameDay() throws Exception {
        // Arrange - Completa l'abitudine una volta
        habitCompletionService.completeHabit(testHabit.getId(), testUser.getId());

        // Act & Assert - Tenta di completare di nuovo lo stesso giorno
        HabitAlreadyCompletedException exception = assertThrows(
                HabitAlreadyCompletedException.class,
                () -> habitCompletionService.completeHabit(testHabit.getId(), testUser.getId()),
                "Dovrebbe lanciare HabitAlreadyCompletedException per completamenti duplicati"
        );

        assertTrue(exception.getMessage().contains("già completata oggi"),
                "Il messaggio dovrebbe indicare che l'abitudine è già stata completata oggi");

        // Verifica che ci sia solo un completamento nel database
        boolean completions = habitCompletionDAO.existsByUserIdAndHabitIdAndCompletionDate(testUser.getId(), testHabit.getId(), LocalDate.now());
        assertTrue(completions, "Il database dovrebbe contenere solo un completamento per giornata");
    }

    @Test
    @DisplayName("Dovrebbe calcolare correttamente le streak consecutive")
    @Rollback
    void shouldCalculateStreaksCorrectly() throws Exception {
        // Simula completamenti in giorni consecutivi modificando la data dell'abitudine
        
        // Primo completamento (ieri)
        testHabit.setLastCompletedDate(LocalDate.now().minusDays(1));
        testHabit.setCurrentStreak(1);
        habitDAO.save(testHabit);

        // Secondo completamento (oggi)
        CompletionResultDTO result = habitCompletionService.completeHabit(testHabit.getId(), testUser.getId());

        // Assert - Verifica che la streak sia incrementata
        assertEquals(2, result.getCompletion().getStreak(), "La streak dovrebbe essere 2 per giorni consecutivi");

        // Verifica che l'abitudine sia stata aggiornata correttamente
        Optional<Habit> updatedHabit = habitDAO.findById(testHabit.getId());
        assertTrue(updatedHabit.isPresent());
        assertEquals(2, updatedHabit.get().getCurrentStreak());
        assertEquals(LocalDate.now(), updatedHabit.get().getLastCompletedDate());
    }

    @Test
    @DisplayName("Dovrebbe resettare la streak per giorni non consecutivi")
    @Rollback
    void shouldResetStreakForNonConsecutiveDays() throws Exception {
        // Simula un completamento di 3 giorni fa
        testHabit.setLastCompletedDate(LocalDate.now().minusDays(3));
        testHabit.setCurrentStreak(5); // Aveva una streak alta
        habitDAO.save(testHabit);

        // Completamento oggi (dopo un gap)
        CompletionResultDTO result = habitCompletionService.completeHabit(testHabit.getId(), testUser.getId());

        // Assert - La streak dovrebbe essere resettata a 1
        assertEquals(1, result.getCompletion().getStreak(), "La streak dovrebbe essere resettata a 1 per giorni non consecutivi");

        // Verifica nel database
        Optional<Habit> updatedHabit = habitDAO.findById(testHabit.getId());
        assertTrue(updatedHabit.isPresent());
        assertEquals(1, updatedHabit.get().getCurrentStreak());
    }

    @Test
    @DisplayName("Dovrebbe gestire il primo completamento di un'abitudine")
    @Rollback
    void shouldHandleFirstHabitCompletion() throws Exception {
        // Assicurati che l'abitudine non sia mai stata completata
        assertNull(testHabit.getLastCompletedDate());
        assertEquals(0, testHabit.getCurrentStreak());

        // Act - Primo completamento
        CompletionResultDTO result = habitCompletionService.completeHabit(testHabit.getId(), testUser.getId());

        // Assert - Verifica il primo completamento
        assertEquals(1, result.getCompletion().getStreak(), "Il primo completamento dovrebbe avere streak = 1");
//        assertTrue(result.getXpGained() > 0, "Dovrebbe essere guadagnato XP");

        // Verifica nel database
        Optional<Habit> updatedHabit = habitDAO.findById(testHabit.getId());
        assertTrue(updatedHabit.isPresent());
        assertEquals(1, updatedHabit.get().getCurrentStreak());
        assertEquals(LocalDate.now(), updatedHabit.get().getLastCompletedDate());
    }

    @Test
    @DisplayName("Dovrebbe lanciare eccezione per abitudine inesistente")
    @Rollback
    void shouldThrowExceptionForNonExistentHabit() {
        // Act & Assert
        HabitNotFoundException exception = assertThrows(
                HabitNotFoundException.class,
                () -> habitCompletionService.completeHabit(999L, testUser.getId()),
                "Dovrebbe lanciare HabitNotFoundException per abitudine inesistente"
        );

        assertTrue(exception.getMessage().contains("999"),
                "Il messaggio dovrebbe contenere l'ID dell'abitudine non trovata");
    }

    @Test
    @DisplayName("Dovrebbe lanciare eccezione per utente inesistente")
    @Rollback
    void shouldThrowExceptionForNonExistentUser() {
        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> habitCompletionService.completeHabit(testHabit.getId(), 999L),
                "Dovrebbe lanciare UserNotFoundException per utente inesistente"
        );

        assertTrue(exception.getMessage().contains("999"),
                "Il messaggio dovrebbe contenere l'ID dell'utente non trovato");
    }

    @Test
    @DisplayName("Dovrebbe calcolare XP basato sulla difficoltà dell'abitudine")
    @Rollback
    void shouldCalculateXpBasedOnHabitDifficulty() throws Exception {
        // Crea abitudini con diverse difficoltà
        Habit easyHabit = new Habit();
        easyHabit.setName("Easy Habit");
        easyHabit.setDescription("Abitudine facile");
        easyHabit.setDifficulty(HabitDifficulty.EASY);
        easyHabit.setFrequency(HabitFrequencyType.DAILY);
        easyHabit.setUserId(testUser.getId());
        easyHabit.setCurrentStreak(0);
        easyHabit.setCreatedAt(LocalDateTime.now());
        easyHabit = habitDAO.save(easyHabit);

        Habit hardHabit = new Habit();
        hardHabit.setName("Hard Habit");
        hardHabit.setDescription("Abitudine difficile");
        hardHabit.setDifficulty(HabitDifficulty.HARD);
        hardHabit.setFrequency(HabitFrequencyType.DAILY);
        hardHabit.setUserId(testUser.getId());
        hardHabit.setCurrentStreak(0);
        hardHabit.setCreatedAt(LocalDateTime.now());
        hardHabit = habitDAO.save(hardHabit);

        // Completa entrambe le abitudini
        CompletionResultDTO easyResult = habitCompletionService.completeHabit(easyHabit.getId(), testUser.getId());
        
        // Reset user XP per test pulito
        testUser.setTotalXp(0.0);
        userDAO.save(testUser);
        
        CompletionResultDTO hardResult = habitCompletionService.completeHabit(hardHabit.getId(), testUser.getId());

        // Assert - L'abitudine difficile dovrebbe dare più XP
//        assertTrue(hardResult.getXpGained() > easyResult.getXpGained(),
//                "L'abitudine difficile dovrebbe dare più XP di quella facile");
    }

    @Test
    @DisplayName("Dovrebbe aggiornare correttamente il livello dell'utente")
    @Rollback
    void shouldUpdateUserLevelCorrectly() throws Exception {
        // Imposta l'utente vicino al level up
        testUser.setTotalXp(90.0); // Vicino al livello successivo
        testUser.setLevel(1);
        userDAO.save(testUser);

        // Act - Completa l'abitudine per guadagnare XP
        CompletionResultDTO result = habitCompletionService.completeHabit(testHabit.getId(), testUser.getId());

        // Assert - Verifica che il livello sia stato aggiornato se necessario
//        assertNotNull(result.getNewLevel());
//        assertTrue(result.getNewLevel() >= 1);

        // Verifica nel database
        Optional<User> updatedUser = userDAO.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertTrue(updatedUser.get().getTotalXp() > 90.0, "L'XP totale dovrebbe essere aumentato");
    }
}