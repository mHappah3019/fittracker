package ingsoftware.service;

import ingsoftware.exception.UserNotFoundException;
import ingsoftware.model.User;
import ingsoftware.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void isFirstAccessOfDay_shouldReturnTrue_whenLastAccessDateIsNull() {
        // Arrange
        User user = new User();
        // lastAccessDate is null by default
        LocalDate currentDate = LocalDate.now();

        // Act
        boolean result = userService.isFirstAccessOfDay(user, currentDate);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void isFirstAccessOfDay_shouldReturnTrue_whenLastAccessDateIsBeforeCurrentDate() {
        // Arrange
        User user = new User();
        LocalDate currentDate = LocalDate.now();
        LocalDate lastAccessDate = currentDate.minusDays(1);
        user.setLastAccessDate(lastAccessDate);

        // Act
        boolean result = userService.isFirstAccessOfDay(user, currentDate);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void isFirstAccessOfDay_shouldReturnFalse_whenLastAccessDateIsSameAsCurrentDate() {
        // Arrange
        User user = new User();
        LocalDate currentDate = LocalDate.now();
        user.setLastAccessDate(currentDate);

        // Act
        boolean result = userService.isFirstAccessOfDay(user, currentDate);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void isFirstAccessOfDay_shouldReturnFalse_whenLastAccessDateIsAfterCurrentDate() {
        // Arrange
        User user = new User();
        LocalDate currentDate = LocalDate.now();
        LocalDate lastAccessDate = currentDate.plusDays(1);
        user.setLastAccessDate(lastAccessDate);

        // Act
        boolean result = userService.isFirstAccessOfDay(user, currentDate);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void saveUser_shouldCallRepositorySave() {
        // Arrange
        User user = new User();
        user.setId(1L);

        // Act
        userService.saveUser(user);

        // Assert
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findUserOrThrow_shouldReturnUser_whenUserExists() {
        // Arrange
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.findUserOrThrow(userId);

        // Assert
        assertThat(result).isEqualTo(expectedUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserOrThrow_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findUserOrThrow(userId))
                .isInstanceOf(UserNotFoundException.class);
        
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void checkDefaultUser_shouldReturnTrue_whenNoUsersExist() {
        // Arrange
        when(userRepository.count()).thenReturn(0L);

        // Act
        boolean result = userService.checkDefaultUser();

        // Assert
        assertThat(result).isTrue();
        verify(userRepository, times(1)).count();
    }

    @Test
    void checkDefaultUser_shouldReturnFalse_whenUsersExist() {
        // Arrange
        when(userRepository.count()).thenReturn(5L);

        // Act
        boolean result = userService.checkDefaultUser();

        // Assert
        assertThat(result).isFalse();
        verify(userRepository, times(1)).count();
    }
}