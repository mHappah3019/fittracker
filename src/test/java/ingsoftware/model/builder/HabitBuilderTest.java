package ingsoftware.model.builder;

import ingsoftware.model.Habit;
import ingsoftware.model.enum_helpers.HabitDifficulty;
import ingsoftware.model.enum_helpers.HabitFrequencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HabitBuilderTest {

    private HabitBuilder habitBuilder;

    @BeforeEach
    void setUp() {
        habitBuilder = new HabitBuilder();
    }

    @Test
    void build_shouldCreateHabitWithRequiredFields() {
        // Arrange
        Long userId = 1L;
        String name = "Test Habit";
        String description = "Test Description";

        // Act
        Habit result = habitBuilder
                .withUserId(userId)
                .withName(name)
                .withDescription(description)
                .build();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getFrequency()).isEqualTo(HabitFrequencyType.DAILY); // default
        assertThat(result.getDifficulty()).isEqualTo(HabitDifficulty.MEDIUM); // default
    }


    @Test
    void build_shouldThrowException_whenNameIsWhitespace() {
        // Act & Assert
        assertThatThrownBy(() -> habitBuilder.withName("   ").build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Il nome dell'abitudine è obbligatorio");
    }

    @Test
    void build_shouldThrowException_whenNameIsTooLong() {
        // Arrange
        String longName = "a".repeat(101); // 101 characters

        // Act & Assert
        assertThatThrownBy(() -> habitBuilder.withName(longName).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Il nome non può superare i 100 caratteri");
    }

    @Test
    void build_shouldAcceptMaxLengthName() {
        // Arrange
        String maxLengthName = "a".repeat(100); // exactly 100 characters

        // Act
        Habit result = habitBuilder.withName(maxLengthName).build();

        // Assert
        assertThat(result.getName()).isEqualTo(maxLengthName);
        assertThat(result.getName()).hasSize(100);
    }
}