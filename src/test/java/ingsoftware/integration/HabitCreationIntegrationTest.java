package ingsoftware.integration;

import ingsoftware.controller.CreateHabitController;
import ingsoftware.dao.HabitDAO;
import ingsoftware.dao.UserDAO;
import ingsoftware.model.Habit;
import ingsoftware.model.User;
import ingsoftware.model.builder.HabitBuilder;
import ingsoftware.model.enum_helpers.HabitDifficulty;
import ingsoftware.model.enum_helpers.HabitFrequencyType;
import ingsoftware.service.HabitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test per il flusso completo di creazione abitudini.
 * Testa l'integrazione tra CreateHabitController, HabitService e HabitDAO.
 */
@DisplayName("Integration Test - Creazione Abitudini")
class HabitCreationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private HabitService habitService;

    @Autowired
    private HabitDAO habitDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CreateHabitController createHabitController;

    private User testUser;
    private HabitBuilder validHabitBuilder;

    @BeforeEach
    void setUp() {
        // Crea un utente di test
        testUser = new User();
        testUser.setUsername("testuser");
        testUser = userDAO.save(testUser);

        // Crea un builder per abitudine valida
        validHabitBuilder = new HabitBuilder()
                .withName("Bere 8 bicchieri d'acqua")
                .withDescription("Mantenersi idratati durante il giorno")
                .withDifficulty(HabitDifficulty.EASY)
                .withFrequency(HabitFrequencyType.DAILY)
                .withUserId(testUser.getId());
    }

    @Test
    @DisplayName("Dovrebbe creare una nuova abitudine con successo")
    @Rollback
    void shouldCreateNewHabitSuccessfully() throws Exception {
        // Act - Crea l'abitudine attraverso il service
        Habit createdHabit = habitService.createHabit(validHabitBuilder);

        // Assert - Verifica che l'abitudine sia stata creata correttamente
        assertNotNull(createdHabit, "L'abitudine creata non dovrebbe essere null");
        assertNotNull(createdHabit.getId(), "L'ID dell'abitudine dovrebbe essere assegnato");
        assertEquals("Bere 8 bicchieri d'acqua", createdHabit.getName());
        assertEquals("Mantenersi idratati durante il giorno", createdHabit.getDescription());
        assertEquals(HabitDifficulty.EASY, createdHabit.getDifficulty());
        assertEquals(HabitFrequencyType.DAILY, createdHabit.getFrequency());
        assertEquals(testUser.getId(), createdHabit.getUserId());
        assertNotNull(createdHabit.getCreatedAt(), "La data di creazione dovrebbe essere impostata");

        // Verifica che l'abitudine sia stata persistita nel database
        Optional<Habit> persistedHabit = habitDAO.findById(createdHabit.getId());
        assertTrue(persistedHabit.isPresent(), "L'abitudine dovrebbe essere presente nel database");
        assertEquals(createdHabit.getName(), persistedHabit.get().getName());
    }


    @Test
    @DisplayName("Dovrebbe gestire correttamente la ricerca per nome utente")
    @Rollback
    void shouldHandleUserHabitSearchCorrectly() throws Exception {
        // Arrange - Crea multiple abitudini per l'utente
        HabitBuilder habit1Builder = validHabitBuilder.withName("Abitudine 1");
        HabitBuilder habit2Builder = new HabitBuilder()
                .withName("Abitudine 2")
                .withDescription("Seconda abitudine")
                .withDifficulty(HabitDifficulty.MEDIUM)
                .withFrequency(HabitFrequencyType.DAILY)
                .withUserId(testUser.getId());

        // Act - Crea le abitudini
        Habit habit1 = habitService.createHabit(habit1Builder);
        Habit habit2 = habitService.createHabit(habit2Builder);

        // Assert - Verifica la ricerca per nome specifico
        Optional<Habit> foundHabit1 = habitDAO.findByUserIdAndName(testUser.getId(), "Abitudine 1");
        Optional<Habit> foundHabit2 = habitDAO.findByUserIdAndName(testUser.getId(), "Abitudine 2");
        Optional<Habit> notFoundHabit = habitDAO.findByUserIdAndName(testUser.getId(), "Abitudine Inesistente");

        assertTrue(foundHabit1.isPresent());
        assertTrue(foundHabit2.isPresent());
        assertFalse(notFoundHabit.isPresent());

        assertEquals(habit1.getId(), foundHabit1.get().getId());
        assertEquals(habit2.getId(), foundHabit2.get().getId());

        // Verifica che tutte le abitudini dell'utente siano recuperabili
        List<Habit> allUserHabits = habitDAO.findAllByUserId(testUser.getId());
        assertEquals(2, allUserHabits.size());
    }

    @Test
    @DisplayName("Dovrebbe validare correttamente i dati dell'abitudine")
    @Rollback
    void shouldValidateHabitDataCorrectly() throws Exception {
        // Test con diversi livelli di difficoltà e frequenze
        HabitBuilder easyDailyHabit = new HabitBuilder()
                .withName("Abitudine Facile")
                .withDescription("Test")
                .withDifficulty(HabitDifficulty.EASY)
                .withFrequency(HabitFrequencyType.DAILY)
                .withUserId(testUser.getId());

        HabitBuilder hardDailyHabit = new HabitBuilder()
                .withName("Abitudine Difficile")
                .withDescription("Test")
                .withDifficulty(HabitDifficulty.HARD)
                .withFrequency(HabitFrequencyType.DAILY)
                .withUserId(testUser.getId());

        // Act - Crea abitudini con diversi parametri
        Habit easyHabit = habitService.createHabit(easyDailyHabit);
        Habit hardHabit = habitService.createHabit(hardDailyHabit);

        // Assert - Verifica che i parametri siano stati salvati correttamente
        assertEquals(HabitDifficulty.EASY, easyHabit.getDifficulty());
        assertEquals(HabitDifficulty.HARD, hardHabit.getDifficulty());
        assertEquals(HabitFrequencyType.DAILY, easyHabit.getFrequency());
        assertEquals(HabitFrequencyType.DAILY, hardHabit.getFrequency());

        // Verifica persistenza
        Optional<Habit> persistedEasy = habitDAO.findById(easyHabit.getId());
        Optional<Habit> persistedHard = habitDAO.findById(hardHabit.getId());

        assertTrue(persistedEasy.isPresent());
        assertTrue(persistedHard.isPresent());
        assertEquals(HabitDifficulty.EASY, persistedEasy.get().getDifficulty());
        assertEquals(HabitDifficulty.HARD, persistedHard.get().getDifficulty());
    }
}