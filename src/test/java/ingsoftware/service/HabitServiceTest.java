package ingsoftware.service;

import ingsoftware.exception.DuplicateHabitException;
import ingsoftware.exception.HabitNotFoundException;
import ingsoftware.model.Habit;
import ingsoftware.model.builder.HabitBuilder;
import ingsoftware.repository.HabitRepository;
import ingsoftware.service.HabitService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @InjectMocks
    private HabitService habitService;

    public HabitServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveHabit_shouldCreateNewHabit_whenHabitIdIsNull() {
        // Arrange
        HabitBuilder builder = new HabitBuilder()
                .withName("Test Habit")
                .withUserId(1L)
                .withDescription("Test Description");

        Habit expectedHabit = builder.build();

        when(habitRepository.findByUserIdAndName(1L, "Test Habit")).thenReturn(Optional.empty());
        when(habitRepository.save(any(Habit.class))).thenReturn(expectedHabit);

        // Act
        Habit result = habitService.saveHabit(null, builder);

        // Assert
        assertThat(result).isEqualTo(expectedHabit);
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    void saveHabit_shouldThrowException_whenDuplicateHabitExistsForNewHabit() {
        // Arrange
        HabitBuilder builder = new HabitBuilder()
                .withName("Duplicate Habit")
                .withUserId(1L)
                .withDescription("Another Test Description");

        Habit duplicateHabit = builder.build();

        when(habitRepository.findByUserIdAndName(1L, "Duplicate Habit")).thenReturn(Optional.of(duplicateHabit));

        // Act & Assert
        assertThatThrownBy(() -> habitService.saveHabit(null, builder))
                .isInstanceOf(DuplicateHabitException.class)
                .hasMessage("Abitudine con questo nome già esistente");
    }

    @Test
    void saveHabit_shouldUpdateExistingHabit_whenHabitIdIsProvided() {
        // Arrange
        HabitBuilder builder = new HabitBuilder()
                .withId(1L)
                .withUserId(1L)
                .withName("Updated Habit")
                .withDescription("Updated Description");

        Habit existingHabit = new Habit();
        existingHabit.setId(1L);
        existingHabit.setName("Old Habit");
        existingHabit.setUserId(1L);

        Habit updatedHabit = builder.build();

        when(habitRepository.findById(1L)).thenReturn(Optional.of(existingHabit));
        when(habitRepository.findByUserIdAndName(1L, "Updated Habit")).thenReturn(Optional.empty());
        when(habitRepository.save(any(Habit.class))).thenReturn(updatedHabit);

        // Act
        Habit result = habitService.saveHabit(1L, builder);

        // Assert
        assertThat(result).isEqualTo(updatedHabit);
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    void saveHabit_shouldThrowException_whenUpdatingNonExistingHabit() {
        // Arrange
        HabitBuilder builder = new HabitBuilder()
                .withId(1L)
                .withUserId(1L)
                .withName("Non-Existent Habit")
                .withDescription("Description");

        when(habitRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> habitService.saveHabit(1L, builder))
                .isInstanceOf(HabitNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void saveHabit_shouldThrowException_whenDuplicateHabitExistsForUpdate() {
        // Arrange
        HabitBuilder builder = new HabitBuilder()
                .withId(2L)
                .withUserId(1L)
                .withName("Duplicate Habit")
                .withDescription("Description");

        Habit existingHabit = new Habit();
        existingHabit.setId(2L);
        existingHabit.setName("Old Habit");
        existingHabit.setUserId(1L);

        Habit duplicateHabit = new Habit();
        duplicateHabit.setId(3L);
        duplicateHabit.setName("Duplicate Habit");
        duplicateHabit.setUserId(1L);

        when(habitRepository.findById(2L)).thenReturn(Optional.of(existingHabit));
        when(habitRepository.findByUserIdAndName(1L, "Duplicate Habit")).thenReturn(Optional.of(duplicateHabit));

        // Act & Assert
        assertThatThrownBy(() -> habitService.saveHabit(2L, builder))
                .isInstanceOf(DuplicateHabitException.class)
                .hasMessage("Abitudine con questo nome già esistente.");
    }
}