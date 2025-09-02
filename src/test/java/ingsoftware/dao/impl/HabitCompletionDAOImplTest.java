package ingsoftware.dao.impl;

import ingsoftware.model.HabitCompletion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
 * Test per HabitCompletionDAOImpl - Tests semplici e comprensibili
 * Copre i casi principali delle query JPQL personalizzate
 */
@ExtendWith(MockitoExtension.class)
class HabitCompletionDAOImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @InjectMocks
    private HabitCompletionDAOImpl habitCompletionDAO;

    private HabitCompletion habitCompletion1;
    private HabitCompletion habitCompletion2;

    @BeforeEach
    void setUp() {
        // Crea completamenti di abitudini di test
        habitCompletion1 = new HabitCompletion();
        habitCompletion1.setId(1L);
        habitCompletion1.setUserId(100L);
        habitCompletion1.setHabitId(1L);
        habitCompletion1.setCompletionDate(LocalDate.now());

        habitCompletion2 = new HabitCompletion();
        habitCompletion2.setId(2L);
        habitCompletion2.setUserId(200L);
        habitCompletion2.setHabitId(2L);
        habitCompletion2.setCompletionDate(LocalDate.now().minusDays(1));
    }

    // ===========================================
    // TEST PER existsByUserIdAndHabitIdAndCompletionDate()
    // ===========================================

    @Test
    void testExistsByUserIdAndHabitIdAndCompletionDate_WhenExists_ReturnsTrue() {
        // Arrange
        Long userId = 100L;
        Long habitId = 1L;
        LocalDate completionDate = LocalDate.now();
        
        when(entityManager.createQuery(eq("SELECT COUNT(hc) FROM HabitCompletion hc WHERE hc.userId = :userId AND hc.habitId = :habitId AND hc.completionDate = :completionDate"), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(1L);

        // Act
        boolean result = habitCompletionDAO.existsByUserIdAndHabitIdAndCompletionDate(userId, habitId, completionDate);

        // Assert
        assertTrue(result, "Dovrebbe restituire true quando il completamento esiste");
        
        // Verifica che la query sia stata eseguita correttamente
        verify(entityManager).createQuery(
                "SELECT COUNT(hc) FROM HabitCompletion hc WHERE hc.userId = :userId AND hc.habitId = :habitId AND hc.completionDate = :completionDate", 
                Long.class);
        verify(longTypedQuery).setParameter("userId", userId);
        verify(longTypedQuery).setParameter("habitId", habitId);
        verify(longTypedQuery).setParameter("completionDate", completionDate);
        verify(longTypedQuery).getSingleResult();
    }

    @Test
    void testExistsByUserIdAndHabitIdAndCompletionDate_WhenDoesNotExist_ReturnsFalse() {
        // Arrange
        Long userId = 999L;
        Long habitId = 999L;
        LocalDate completionDate = LocalDate.now();
        
        when(entityManager.createQuery(eq("SELECT COUNT(hc) FROM HabitCompletion hc WHERE hc.userId = :userId AND hc.habitId = :habitId AND hc.completionDate = :completionDate"), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);

        // Act
        boolean result = habitCompletionDAO.existsByUserIdAndHabitIdAndCompletionDate(userId, habitId, completionDate);

        // Assert
        assertFalse(result, "Dovrebbe restituire false quando il completamento non esiste");
        
        verify(entityManager).createQuery(
                "SELECT COUNT(hc) FROM HabitCompletion hc WHERE hc.userId = :userId AND hc.habitId = :habitId AND hc.completionDate = :completionDate", 
                Long.class);
        verify(longTypedQuery).setParameter("userId", userId);
        verify(longTypedQuery).setParameter("habitId", habitId);
        verify(longTypedQuery).setParameter("completionDate", completionDate);
        verify(longTypedQuery).getSingleResult();
    }

    @Test
    void testExistsByUserIdAndHabitIdAndCompletionDate_WithDifferentUserId_ReturnsFalse() {
        // Arrange
        Long differentUserId = 300L;
        Long habitId = 1L;
        LocalDate completionDate = LocalDate.now();
        
        when(entityManager.createQuery(eq("SELECT COUNT(hc) FROM HabitCompletion hc WHERE hc.userId = :userId AND hc.habitId = :habitId AND hc.completionDate = :completionDate"), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);

        // Act
        boolean result = habitCompletionDAO.existsByUserIdAndHabitIdAndCompletionDate(differentUserId, habitId, completionDate);

        // Assert
        assertFalse(result, "Dovrebbe restituire false quando l'userId è diverso");
        
        verify(longTypedQuery).setParameter("userId", differentUserId);
        verify(longTypedQuery).setParameter("habitId", habitId);
        verify(longTypedQuery).setParameter("completionDate", completionDate);
    }

    @Test
    void testExistsByUserIdAndHabitIdAndCompletionDate_WithDifferentHabitId_ReturnsFalse() {
        // Arrange
        Long userId = 100L;
        Long differentHabitId = 999L;
        LocalDate completionDate = LocalDate.now();
        
        when(entityManager.createQuery(eq("SELECT COUNT(hc) FROM HabitCompletion hc WHERE hc.userId = :userId AND hc.habitId = :habitId AND hc.completionDate = :completionDate"), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);

        // Act
        boolean result = habitCompletionDAO.existsByUserIdAndHabitIdAndCompletionDate(userId, differentHabitId, completionDate);

        // Assert
        assertFalse(result, "Dovrebbe restituire false quando l'habitId è diverso");
        
        verify(longTypedQuery).setParameter("userId", userId);
        verify(longTypedQuery).setParameter("habitId", differentHabitId);
        verify(longTypedQuery).setParameter("completionDate", completionDate);
    }

    @Test
    void testExistsByUserIdAndHabitIdAndCompletionDate_WithDifferentDate_ReturnsFalse() {
        // Arrange
        Long userId = 100L;
        Long habitId = 1L;
        LocalDate differentDate = LocalDate.now().minusDays(10);
        
        when(entityManager.createQuery(eq("SELECT COUNT(hc) FROM HabitCompletion hc WHERE hc.userId = :userId AND hc.habitId = :habitId AND hc.completionDate = :completionDate"), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);

        // Act
        boolean result = habitCompletionDAO.existsByUserIdAndHabitIdAndCompletionDate(userId, habitId, differentDate);

        // Assert
        assertFalse(result, "Dovrebbe restituire false quando la data di completamento è diversa");
        
        verify(longTypedQuery).setParameter("userId", userId);
        verify(longTypedQuery).setParameter("habitId", habitId);
        verify(longTypedQuery).setParameter("completionDate", differentDate);
    }
}