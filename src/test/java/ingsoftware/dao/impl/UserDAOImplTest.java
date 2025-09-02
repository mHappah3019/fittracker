package ingsoftware.dao.impl;

import ingsoftware.model.User;
import jakarta.persistence.EntityManager;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test per UserDAOImpl - Tests semplici e comprensibili
 * Copre i casi principali delle query JPQL personalizzate
 */
@ExtendWith(MockitoExtension.class)
class UserDAOImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @InjectMocks
    private UserDAOImpl userDAO;

    private User activeUser1;
    private User activeUser2;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        // Crea utenti di test
        activeUser1 = new User();
        activeUser1.setId(1L);
        activeUser1.setUsername("user1");
        activeUser1.setLastAccessDate(LocalDate.now().minusDays(1));

        activeUser2 = new User();
        activeUser2.setId(2L);
        activeUser2.setUsername("user2");
        activeUser2.setLastAccessDate(LocalDate.now().minusDays(2));

        inactiveUser = new User();
        inactiveUser.setId(3L);
        inactiveUser.setUsername("inactiveUser");
        inactiveUser.setLastAccessDate(null);
    }

    // ===========================================
    // TEST PER findAllActiveUserIds()
    // ===========================================

    @Test
    void testFindAllActiveUserIds_WhenActiveUsersExist_ReturnsActiveUserIds() {
        // Arrange
        List<Long> activeUserIds = Arrays.asList(1L, 2L);
        int offset = 0;
        int limit = 10;
        
        when(entityManager.createQuery(eq("SELECT u.id FROM User u WHERE u.lastAccessDate IS NOT NULL ORDER BY u.id"), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getResultList()).thenReturn(activeUserIds);

        // Act
        List<Long> result = userDAO.findAllActiveUserIds(offset, limit);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertEquals(2, result.size(), "Dovrebbe restituire 2 ID di utenti attivi");
        assertTrue(result.contains(1L), "Dovrebbe contenere l'ID del primo utente attivo");
        assertTrue(result.contains(2L), "Dovrebbe contenere l'ID del secondo utente attivo");
        
        // Verifica che la query sia stata eseguita correttamente con paginazione
        verify(entityManager).createQuery("SELECT u.id FROM User u WHERE u.lastAccessDate IS NOT NULL ORDER BY u.id", Long.class);
        verify(longTypedQuery).setFirstResult(offset);
        verify(longTypedQuery).setMaxResults(limit);
        verify(longTypedQuery).getResultList();
    }

    @Test
    void testFindAllActiveUserIds_WhenNoActiveUsers_ReturnsEmptyList() {
        // Arrange
        int offset = 0;
        int limit = 10;
        
        when(entityManager.createQuery(eq("SELECT u.id FROM User u WHERE u.lastAccessDate IS NOT NULL ORDER BY u.id"), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        List<Long> result = userDAO.findAllActiveUserIds(offset, limit);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertTrue(result.isEmpty(), "Dovrebbe restituire una lista vuota quando non ci sono utenti attivi");
        
        verify(entityManager).createQuery("SELECT u.id FROM User u WHERE u.lastAccessDate IS NOT NULL ORDER BY u.id", Long.class);
        verify(longTypedQuery).setFirstResult(offset);
        verify(longTypedQuery).setMaxResults(limit);
        verify(longTypedQuery).getResultList();
    }
}