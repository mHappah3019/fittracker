package ingsoftware.dao.impl;

import ingsoftware.model.Habit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test per HabitDAOImpl - Tests semplici e comprensibili
 * Copre i casi principali delle query JPQL personalizzate
 */
@ExtendWith(MockitoExtension.class)
class HabitDAOImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Habit> habitTypedQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @InjectMocks
    private HabitDAOImpl habitDAO;

    private Habit habit1;
    private Habit habit2;
    private Habit habitWithCompletion;

    @BeforeEach
    void setUp() {
        // Crea abitudini di test
        habit1 = new Habit();
        habit1.setId(1L);
        habit1.setName("Bere Acqua");
        habit1.setUserId(100L);
        habit1.setLastCompletedDate(LocalDate.now().minusDays(1));

        habit2 = new Habit();
        habit2.setId(2L);
        habit2.setName("Esercizio Fisico");
        habit2.setUserId(100L);
        habit2.setLastCompletedDate(LocalDate.now().minusDays(2));

        habitWithCompletion = new Habit();
        habitWithCompletion.setId(3L);
        habitWithCompletion.setName("Meditazione");
        habitWithCompletion.setUserId(200L);
        habitWithCompletion.setLastCompletedDate(LocalDate.now());
    }


    // ===========================================
    // TEST PER findAllByUserId()
    // ===========================================

    @Test
    void testFindAllByUserId_WhenHabitsExist_ReturnsUserHabits() {
        // Arrange
        Long userId = 100L;
        List<Habit> userHabits = Arrays.asList(habit1, habit2);
        
        when(entityManager.createQuery(eq("SELECT h FROM Habit h WHERE h.userId = :userId"), eq(Habit.class)))
                .thenReturn(habitTypedQuery);
        when(habitTypedQuery.getResultList()).thenReturn(userHabits);

        // Act
        List<Habit> result = habitDAO.findAllByUserId(userId);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertEquals(2, result.size(), "Dovrebbe restituire 2 abitudini dell'utente");
        assertTrue(result.contains(habit1), "Dovrebbe contenere la prima abitudine");
        assertTrue(result.contains(habit2), "Dovrebbe contenere la seconda abitudine");
        
        // Verifica che la query sia stata eseguita correttamente
        verify(entityManager).createQuery("SELECT h FROM Habit h WHERE h.userId = :userId", Habit.class);
        verify(habitTypedQuery).setParameter("userId", userId);
        verify(habitTypedQuery).getResultList();
    }

    @Test
    void testFindAllByUserId_WhenNoHabitsExist_ReturnsEmptyList() {
        // Arrange
        Long userId = 999L;
        
        when(entityManager.createQuery(eq("SELECT h FROM Habit h WHERE h.userId = :userId"), eq(Habit.class)))
                .thenReturn(habitTypedQuery);
        when(habitTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        List<Habit> result = habitDAO.findAllByUserId(userId);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertTrue(result.isEmpty(), "Dovrebbe restituire una lista vuota quando l'utente non ha abitudini");
        
        verify(entityManager).createQuery("SELECT h FROM Habit h WHERE h.userId = :userId", Habit.class);
        verify(habitTypedQuery).setParameter("userId", userId);
        verify(habitTypedQuery).getResultList();
    }

    // ===========================================
    // TEST PER findByUserIdAndName()
    // ===========================================

    @Test
    void testFindByUserIdAndName_WhenHabitExists_ReturnsHabit() {
        // Arrange
        Long userId = 100L;
        String habitName = "Bere Acqua";
        
        when(entityManager.createQuery(eq("SELECT h FROM Habit h WHERE h.userId = :userId AND h.name = :name"), eq(Habit.class)))
                .thenReturn(habitTypedQuery);
        when(habitTypedQuery.getSingleResult()).thenReturn(habit1);

        // Act
        Optional<Habit> result = habitDAO.findByUserIdAndName(userId, habitName);

        // Assert
        assertTrue(result.isPresent(), "Dovrebbe trovare l'abitudine");
        assertEquals(habit1, result.get(), "Dovrebbe restituire l'abitudine corretta");
        
        verify(entityManager).createQuery("SELECT h FROM Habit h WHERE h.userId = :userId AND h.name = :name", Habit.class);
        verify(habitTypedQuery).setParameter("userId", userId);
        verify(habitTypedQuery).setParameter("name", habitName);
        verify(habitTypedQuery).getSingleResult();
    }

    @Test
    void testFindByUserIdAndName_WhenHabitDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        Long userId = 100L;
        String habitName = "Abitudine Inesistente";
        
        when(entityManager.createQuery(eq("SELECT h FROM Habit h WHERE h.userId = :userId AND h.name = :name"), eq(Habit.class)))
                .thenReturn(habitTypedQuery);
        when(habitTypedQuery.getSingleResult()).thenThrow(new NoResultException("Nessun risultato trovato"));

        // Act
        Optional<Habit> result = habitDAO.findByUserIdAndName(userId, habitName);

        // Assert
        assertFalse(result.isPresent(), "Dovrebbe restituire Optional vuoto quando l'abitudine non esiste");
        
        verify(entityManager).createQuery("SELECT h FROM Habit h WHERE h.userId = :userId AND h.name = :name", Habit.class);
        verify(habitTypedQuery).setParameter("userId", userId);
        verify(habitTypedQuery).setParameter("name", habitName);
        verify(habitTypedQuery).getSingleResult();
    }
}