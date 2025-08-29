package ingsoftware.service;

import ingsoftware.dao.HabitDAO;
import ingsoftware.exception.BusinessException;
import ingsoftware.exception.DuplicateHabitException;
import ingsoftware.exception.HabitNotFoundException;
import ingsoftware.model.Habit;
import ingsoftware.model.builder.HabitBuilder;
import ingsoftware.model.enum_helpers.HabitDifficulty;
import ingsoftware.model.enum_helpers.HabitFrequencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test per HabitService - Tests semplici e comprensibili
 * Copre i casi principali di gestione delle abitudini
 */
@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock
    private HabitDAO habitDAO;

    @InjectMocks
    private HabitService habitService;

    private Habit testHabit;
    private HabitBuilder habitBuilder;
    private Long userId = 1L;
    private Long habitId = 1L;
    
    @BeforeEach
    void setUp() {
        // Crea un'abitudine di test
        testHabit = new Habit();
        testHabit.setId(habitId);
        testHabit.setName("Test Habit");
        testHabit.setDescription("Descrizione di test");
        testHabit.setDifficulty(HabitDifficulty.MEDIUM);
        testHabit.setFrequency(HabitFrequencyType.DAILY);
        testHabit.setUserId(userId);
        testHabit.setCreatedAt(LocalDateTime.now());
        
        // Crea un builder di test
        habitBuilder = new HabitBuilder()
                .withName("Test Habit")
                .withDescription("Descrizione di test")
                .withDifficulty(HabitDifficulty.MEDIUM)
                .withFrequency(HabitFrequencyType.DAILY)
                .withUserId(userId);
    }

    // ===========================================
    // TEST PER createHabit()
    // ===========================================

    @Test
    void testCreateHabit_ValidHabit_ReturnsCreatedHabit() throws BusinessException {
        // Arrange
        when(habitDAO.findByUserIdAndName(userId, "Test Habit")).thenReturn(Optional.empty());
        when(habitDAO.save(any(Habit.class))).thenReturn(testHabit);

        // Act
        Habit result = habitService.createHabit(habitBuilder);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertEquals("Test Habit", result.getName(), "Il nome dovrebbe corrispondere");
        assertEquals(userId, result.getUserId(), "L'ID utente dovrebbe corrispondere");
        
        verify(habitDAO).findByUserIdAndName(userId, "Test Habit");
        verify(habitDAO).save(any(Habit.class));
    }

    @Test
    void testCreateHabit_DuplicateName_ThrowsException() {
        // Arrange: abitudine con stesso nome già esistente
        when(habitDAO.findByUserIdAndName(userId, "Test Habit")).thenReturn(Optional.of(testHabit));

        // Act & Assert
        DuplicateHabitException exception = assertThrows(DuplicateHabitException.class,
                () -> habitService.createHabit(habitBuilder),
                "Dovrebbe lanciare DuplicateHabitException per nomi duplicati");
        
        assertTrue(exception.getMessage().contains("già esistente"), 
                  "Il messaggio dovrebbe indicare che l'abitudine esiste già");
        
        // Verifica che save non sia stato chiamato
        verify(habitDAO, never()).save(any(Habit.class));
    }


    // ===========================================
    // TEST PER updateHabit()
    // ===========================================

    @Test
    void testUpdateHabit_ValidUpdate_ReturnsUpdatedHabit() throws BusinessException {
        // Arrange
        HabitBuilder updateBuilder = new HabitBuilder()
                .withName("Updated Habit")
                .withDescription("Nuova descrizione")
                .withDifficulty(HabitDifficulty.HARD)
                .withFrequency(HabitFrequencyType.DAILY)
                .withUserId(userId);
        
        when(habitDAO.findById(habitId)).thenReturn(Optional.of(testHabit));
        when(habitDAO.findByUserIdAndName(userId, "Updated Habit")).thenReturn(Optional.empty());
        when(habitDAO.save(any(Habit.class))).thenReturn(testHabit);

        // Act
        Habit result = habitService.updateHabit(habitId, updateBuilder);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        verify(habitDAO).findById(habitId);
        verify(habitDAO).save(testHabit);
        
        // Verifica che l'abitudine sia stata aggiornata (attraverso le chiamate ai setter)
        assertEquals("Updated Habit", testHabit.getName());
        assertEquals("Nuova descrizione", testHabit.getDescription());
        assertEquals(HabitDifficulty.HARD, testHabit.getDifficulty());
    }

    @Test
    void testUpdateHabit_HabitNotFound_ThrowsException() {
        // Arrange
        when(habitDAO.findById(habitId)).thenReturn(Optional.empty());

        // Act & Assert
        HabitNotFoundException exception = assertThrows(HabitNotFoundException.class,
                () -> habitService.updateHabit(habitId, habitBuilder),
                "Dovrebbe lanciare HabitNotFoundException quando l'abitudine non esiste");
        
        assertTrue(exception.getMessage().contains(habitId.toString()),
                  "Il messaggio dovrebbe contenere l'ID dell'abitudine non trovata");
    }

    @Test
    void testUpdateHabit_DuplicateNameDifferentHabit_ThrowsException() {
        // Arrange: un'altra abitudine con lo stesso nome esiste già
        Habit anotherHabit = new Habit();
        anotherHabit.setId(2L);
        anotherHabit.setName("Updated Habit");
        
        HabitBuilder updateBuilder = new HabitBuilder()
                .withName("Updated Habit")
                .withDescription("Test")
                .withDifficulty(HabitDifficulty.MEDIUM)
                .withFrequency(HabitFrequencyType.DAILY)
                .withUserId(userId);
        
        when(habitDAO.findById(habitId)).thenReturn(Optional.of(testHabit));
        when(habitDAO.findByUserIdAndName(userId, "Updated Habit")).thenReturn(Optional.of(anotherHabit));

        // Act & Assert
        DuplicateHabitException exception = assertThrows(DuplicateHabitException.class,
                () -> habitService.updateHabit(habitId, updateBuilder),
                "Dovrebbe lanciare DuplicateHabitException quando si tenta di usare un nome già esistente");
        
        verify(habitDAO, never()).save(any(Habit.class));
    }

    // ===========================================
    // TEST PER findAllByUserId()
    // ===========================================

    @Test
    void testFindAllByUserId_ReturnsUserHabits() {
        // Arrange
        Habit habit1 = new Habit();
        habit1.setId(1L);
        habit1.setUserId(userId);
        
        Habit habit2 = new Habit();
        habit2.setId(2L);
        habit2.setUserId(userId);
        
        List<Habit> userHabits = Arrays.asList(habit1, habit2);
        when(habitDAO.findAllByUserId(userId)).thenReturn(userHabits);

        // Act
        List<Habit> result = habitService.findAllByUserId(userId);

        // Assert
        assertEquals(2, result.size(), "Dovrebbe restituire tutte le abitudini dell'utente");
        assertEquals(userHabits, result, "Dovrebbe restituire la lista corretta");
        verify(habitDAO).findAllByUserId(userId);
    }

}