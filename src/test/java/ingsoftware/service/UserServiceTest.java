package ingsoftware.service;

import ingsoftware.dao.UserDAO;
import ingsoftware.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test per UserService - Tests semplici e comprensibili
 * Copre i casi principali di utilizzo del servizio utenti
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    private User testUser;
    
    @BeforeEach
    void setUp() {
        // Crea un utente di test per ogni scenario
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setLevel(2);
        testUser.setLastAccessDate(LocalDate.of(2024, 1, 1));
    }

    // ===========================================
    // TEST PER isFirstAccessOfDay()
    // ===========================================

    @Test
    void testIsFirstAccessOfDay_UserNeverAccessed_ReturnsTrue() {
        // Arrange: utente senza ultimo accesso
        testUser.setLastAccessDate(null);
        LocalDate today = LocalDate.of(2024, 1, 15);

        // Act
        boolean result = userService.isFirstAccessOfDay(testUser, today);

        // Assert
        assertTrue(result, "Il primo accesso in assoluto dovrebbe restituire true");
    }

    @Test
    void testIsFirstAccessOfDay_UserAccessedYesterday_ReturnsTrue() {
        // Arrange: utente ha accesso ieri
        LocalDate yesterday = LocalDate.of(2024, 1, 14);
        LocalDate today = LocalDate.of(2024, 1, 15);
        testUser.setLastAccessDate(yesterday);

        // Act
        boolean result = userService.isFirstAccessOfDay(testUser, today);

        // Assert
        assertTrue(result, "Accesso del giorno precedente dovrebbe restituire true");
    }

    @Test
    void testIsFirstAccessOfDay_UserAccessedToday_ReturnsFalse() {
        // Arrange: utente ha già accesso oggi
        LocalDate today = LocalDate.of(2024, 1, 15);
        testUser.setLastAccessDate(today);

        // Act
        boolean result = userService.isFirstAccessOfDay(testUser, today);

        // Assert
        assertFalse(result, "Se l'utente ha già accesso oggi, dovrebbe restituire false");
    }

    // ===========================================
    // TEST PER saveUser()
    // ===========================================

    @Test
    void testSaveUser_ValidUser_CallsDAOSave() {
        // Act
        userService.saveUser(testUser);

        // Assert: verifica che il DAO sia stato chiamato esattamente una volta
        verify(userDAO, times(1)).save(testUser);
    }

    // ===========================================
    // TEST PER findUserOrThrow()
    // ===========================================

    @Test
    void testFindUserOrThrow_UserExists_ReturnsUser() {
        // Arrange
        Long userId = 1L;
        when(userDAO.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findUserOrThrow(userId);

        // Assert
        assertEquals(testUser, result, "Dovrebbe restituire l'utente trovato");
        verify(userDAO).findById(userId);
    }

    // ===========================================
    // TEST PER checkDefaultUser()
    // ===========================================

    @Test
    void testCheckDefaultUser_NoUsersInDB_ReturnsTrue() {
        // Arrange: database vuoto
        when(userDAO.findAll()).thenReturn(Collections.emptyList());

        // Act
        boolean result = userService.checkDefaultUser();

        // Assert
        assertTrue(result, "Se il database è vuoto, dovrebbe restituire true");
    }

    @Test
    void testCheckDefaultUser_UsersExistInDB_ReturnsFalse() {
        // Arrange: database con utenti
        when(userDAO.findAll()).thenReturn(Arrays.asList(testUser));

        // Act
        boolean result = userService.checkDefaultUser();

        // Assert
        assertFalse(result, "Se ci sono utenti nel database, dovrebbe restituire false");
    }
}