package ingsoftware.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void constructor_shouldCreateUserWithDefaults() {
        // Act
        User newUser = new User();

        // Assert
        assertThat(newUser.getLifePoints()).isEqualTo(100);
    }

    @Test
    void constructor_shouldCreateUserWithParameters() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String passwordHash = "hashedpassword";

        // Act
        User newUser = new User(username, email, passwordHash);

        // Assert
        assertThat(newUser.getLevel()).isEqualTo(1);
        assertThat(newUser.getTotalXp()).isEqualTo(0);
        assertThat(newUser.getLastAccessDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void addLifePoints_shouldIncreaseLifePoints_whenPositiveDelta() {
        // Arrange
        user.addLifePoints(0); // Start at 100
        int initialPoints = user.getLifePoints();

        // Act
        user.addLifePoints(20);

        // Assert
        assertThat(user.getLifePoints()).isEqualTo(initialPoints + 20);
    }

    @Test
    void addLifePoints_shouldDecreaseLifePoints_whenNegativeDelta() {
        // Arrange
        user.addLifePoints(0); // Start at 100
        int initialPoints = user.getLifePoints();

        // Act
        user.addLifePoints(-30);

        // Assert
        assertThat(user.getLifePoints()).isEqualTo(initialPoints - 30);
    }

    @Test
    void addLifePoints_shouldNotGoBelowZero_whenNegativeDeltaExceedsPoints() {
        // Arrange
        user.addLifePoints(0); // Start at 100

        // Act
        user.addLifePoints(-150); // Try to go below zero

        // Assert
        assertThat(user.getLifePoints()).isEqualTo(0);
    }

    @Test
    void addLifePoints_shouldSetToZero_whenExactlyZero() {
        // Arrange
        user.addLifePoints(0); // Start at 100

        // Act
        user.addLifePoints(-100); // Exactly zero

        // Assert
        assertThat(user.getLifePoints()).isEqualTo(0);
    }

    @Test
    void addTotalXp_shouldIncreaseXp_whenPositiveXp() {
        // Arrange
        user.setTotalXp(50);

        // Act
        user.addTotalXp(25.5);

        // Assert
        assertThat(user.getTotalXp()).isEqualTo(75.5);
    }

    @Test
    void addTotalXp_shouldDecreaseXp_whenNegativeXp() {
        // Arrange
        user.setTotalXp(100);

        // Act
        user.addTotalXp(-30.0);

        // Assert
        assertThat(user.getTotalXp()).isEqualTo(70.0);
    }

    @Test
    void addTotalXp_shouldNotGoBelowZero_whenNegativeXpExceedsTotal() {
        // Arrange
        user.setTotalXp(50);

        // Act
        user.addTotalXp(-100.0); // Try to go below zero

        // Assert
        assertThat(user.getTotalXp()).isEqualTo(0.0);
    }

    @Test
    void setLevel_shouldUpdateLevel() {
        // Act
        user.setLevel(5);

        // Assert
        assertThat(user.getLevel()).isEqualTo(5);
    }

    @Test
    void setLastAccessDate_shouldUpdateLastAccessDate() {
        // Arrange
        LocalDate testDate = LocalDate.of(2023, 12, 25);

        // Act
        user.setLastAccessDate(testDate);

        // Assert
        assertThat(user.getLastAccessDate()).isEqualTo(testDate);
    }

    @Test
    void setId_shouldUpdateId() {
        // Arrange
        Long testId = 123L;

        // Act
        user.setId(testId);

        // Assert
        assertThat(user.getID()).isEqualTo(testId);
    }

    @Test
    void setTotalXp_shouldUpdateXpWithIntegerValue() {
        // Act
        user.setTotalXp(150);

        // Assert
        assertThat(user.getTotalXp()).isEqualTo(150.0);
    }

    @Test
    void getHabitCompletions_shouldReturnEmptyListByDefault() {
        // Act
        var completions = user.getHabitCompletions();

        // Assert
        assertThat(completions).isNotNull();
        assertThat(completions).isEmpty();
    }

    @Test
    void setHabitCompletions_shouldUpdateCompletionsList() {
        // Arrange
        var completions = new ArrayList<HabitCompletion>();
        var completion = new HabitCompletion();
        completions.add(completion);

        // Act
        user.setHabitCompletions(completions);

        // Assert
        assertThat(user.getHabitCompletions()).hasSize(1);
        assertThat(user.getHabitCompletions()).contains(completion);
    }

    @Test
    void addTotalXp_shouldHandleDecimalValues() {
        // Arrange
        user.setTotalXp(0);

        // Act
        user.addTotalXp(12.75);
        user.addTotalXp(7.25);

        // Assert
        assertThat(user.getTotalXp()).isEqualTo(20.0);
    }

    @Test
    void addLifePoints_shouldHandleMultipleOperations() {
        // Arrange
        int initialPoints = user.getLifePoints(); // 100

        // Act
        user.addLifePoints(50);  // 150
        user.addLifePoints(-20); // 130
        user.addLifePoints(10);  // 140

        // Assert
        assertThat(user.getLifePoints()).isEqualTo(140);
    }

    @Test
    void addLifePoints_shouldHandleZeroChange() {
        // Arrange
        int initialPoints = user.getLifePoints();

        // Act
        user.addLifePoints(0);

        // Assert
        assertThat(user.getLifePoints()).isEqualTo(initialPoints);
    }

    @Test
    void addTotalXp_shouldHandleZeroChange() {
        // Arrange
        user.setTotalXp(50);

        // Act
        user.addTotalXp(0.0);

        // Assert
        assertThat(user.getTotalXp()).isEqualTo(50.0);
    }
}