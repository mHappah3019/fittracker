package ingsoftware.dao;

import ingsoftware.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test per AbstractJpaDAO - Tests semplici e comprensibili
 * Testa la logica base dei metodi CRUD comuni
 * Utilizza una implementazione concreta per testare la classe astratta
 */
@ExtendWith(MockitoExtension.class)
class AbstractJpaDAOTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<User> userTypedQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    private TestableAbstractJpaDAO testDAO;
    private User testUser;

    @BeforeEach
    void setUp() {
        testDAO = new TestableAbstractJpaDAO();
        testDAO.entityManager = entityManager;
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
    }

    // ===========================================
    // TEST PER save() - NEW ENTITY
    // ===========================================

    @Test
    void testSave_WhenEntityIsNew_CallsPersist() {
        // Arrange
        User newUser = new User();
        newUser.setId(null); // Simula entità nuova
        
        // Act
        User result = testDAO.save(newUser);

        // Assert
        assertSame(newUser, result, "Dovrebbe restituire la stessa entità");
        verify(entityManager).persist(newUser);
        verify(entityManager, never()).merge(any());
    }

    // ===========================================
    // TEST PER save() - EXISTING ENTITY
    // ===========================================

    @Test
    void testSave_WhenEntityIsExisting_CallsMerge() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L); // Simula entità esistente
        User mergedUser = new User();
        mergedUser.setId(1L);
        
        when(entityManager.merge(existingUser)).thenReturn(mergedUser);

        // Act
        User result = testDAO.save(existingUser);

        // Assert
        assertSame(mergedUser, result, "Dovrebbe restituire l'entità merged");
        verify(entityManager).merge(existingUser);
        verify(entityManager, never()).persist(any());
    }

    // ===========================================
    // TEST PER findById()
    // ===========================================

    @Test
    void testFindById_WhenEntityExists_ReturnsOptionalWithEntity() {
        // Arrange
        Long userId = 1L;
        when(entityManager.createQuery(eq("SELECT e FROM User e WHERE e.id = :id"), eq(User.class)))
                .thenReturn(userTypedQuery);
        when(userTypedQuery.getSingleResult()).thenReturn(testUser);

        // Act
        Optional<User> result = testDAO.findById(userId);

        // Assert
        assertTrue(result.isPresent(), "Dovrebbe trovare l'entità");
        assertEquals(testUser, result.get(), "Dovrebbe restituire l'entità corretta");
        
        verify(entityManager).createQuery("SELECT e FROM User e WHERE e.id = :id", User.class);
        verify(userTypedQuery).setParameter("id", userId);
        verify(userTypedQuery).getSingleResult();
    }

    // ===========================================
    // TEST PER findAll()
    // ===========================================

    @Test
    void testFindAll_WhenEntitiesExist_ReturnsAllEntities() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testUser2");
        List<User> allUsers = Arrays.asList(testUser, user2);
        
        when(entityManager.createQuery(eq("SELECT e FROM User e"), eq(User.class)))
                .thenReturn(userTypedQuery);
        when(userTypedQuery.getResultList()).thenReturn(allUsers);

        // Act
        List<User> result = testDAO.findAll();

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertEquals(2, result.size(), "Dovrebbe restituire tutte le entità");
        assertTrue(result.contains(testUser), "Dovrebbe contenere il primo utente");
        assertTrue(result.contains(user2), "Dovrebbe contenere il secondo utente");
        
        verify(entityManager).createQuery("SELECT e FROM User e", User.class);
        verify(userTypedQuery).getResultList();
    }

    // ===========================================
    // TEST PER existsById()
    // ===========================================

    @Test
    void testExistsById_WhenEntityExists_ReturnsTrue() {
        // Arrange
        Long userId = 1L;
        when(entityManager.createQuery(eq("SELECT COUNT(e) FROM User e WHERE e.id = :id"), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(1L);

        // Act
        boolean result = testDAO.existsById(userId);

        // Assert
        assertTrue(result, "Dovrebbe restituire true quando l'entità esiste");
        
        verify(entityManager).createQuery("SELECT COUNT(e) FROM User e WHERE e.id = :id", Long.class);
        verify(longTypedQuery).setParameter("id", userId);
        verify(longTypedQuery).getSingleResult();
    }

    @Test
    void testExistsById_WhenEntityDoesNotExist_ReturnsFalse() {
        // Arrange
        Long userId = 999L;
        when(entityManager.createQuery(eq("SELECT COUNT(e) FROM User e WHERE e.id = :id"), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);

        // Act
        boolean result = testDAO.existsById(userId);

        // Assert
        assertFalse(result, "Dovrebbe restituire false quando l'entità non esiste");
    }

    // ===========================================
    // IMPLEMENTAZIONE CONCRETA PER IL TEST
    // ===========================================
    private static class TestableAbstractJpaDAO extends AbstractJpaDAO<User, Long> {
        @Override
        protected boolean isNew(User entity) {
            return entity.getId() == null;
        }
    }
}