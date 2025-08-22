package ingsoftware.model.builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ingsoftware.model.Habit;
import ingsoftware.model.enum_helpers.HabitDifficulty;
import ingsoftware.model.enum_helpers.HabitFrequencyType;


public class HabitBuilder {
    private Long id; // Popolato solo in aggiornamento/lettura
    private Long userId; // Obbligatorio
    private String name; // Obbligatorio, max 100 caratteri
    private String description;
    private HabitFrequencyType frequency = HabitFrequencyType.DAILY; // DAILY, WEEKLY, MONTHLY
    private HabitDifficulty difficulty = HabitDifficulty.MEDIUM; // Default MEDIUM
    private List<String> tags = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer targetStreak;

    public HabitBuilder() {}

    public HabitBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public HabitBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public HabitBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public HabitBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public HabitBuilder withFrequency(HabitFrequencyType frequency) {
        this.frequency = frequency;
        return this;
    }

    public HabitBuilder withDifficulty(HabitDifficulty difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public HabitBuilder withTags(List<String> tags) {
        this.tags.clear(); // Clear existing tags first
        this.tags.addAll(tags);
        return this;
    }

    public HabitBuilder withTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tag);
        return this;
    }

    public HabitBuilder withTargetStreak(Integer targetStreak) {
        this.targetStreak = targetStreak;
        return this;
    }

    public HabitBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public HabitBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Habit build() throws IllegalArgumentException {
        validateRequired();
        validateBusinessRules();

        Habit habit = new Habit();
        habit.setId(id);
        habit.setUserId(userId);
        habit.setName(name);
        habit.setDescription(description);
        habit.setFrequency(frequency);
        habit.setDifficulty(difficulty);
        habit.setTags(new HashSet<>(tags));

        LocalDateTime now = LocalDateTime.now();
        habit.setCreatedAt(createdAt != null ? createdAt : now);
        habit.setUpdatedAt(updatedAt != null ? updatedAt : now);

        return habit;
    }

    private void validateRequired() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome dell'abitudine è obbligatorio");
        }
        if (frequency == null) {
            throw new IllegalArgumentException("La frequenza è obbligatoria");
        }
        if (difficulty == null) {
            throw new IllegalArgumentException("La difficoltà è obbligatoria");
        }
    }

    private void validateBusinessRules() {
        if (name.length() > 100) {
            throw new IllegalArgumentException("Il nome non può superare i 100 caratteri");
        }
    }
}
