package ingsoftware.model;

import ingsoftware.model.builder.HabitBuilder;
import ingsoftware.model.enum_helpers.HabitDifficulty;
import ingsoftware.model.enum_helpers.HabitFrequencyType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "habits")
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private HabitFrequencyType frequency;
    
    @Column(nullable = false)
    private HabitDifficulty difficulty = HabitDifficulty.MEDIUM;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> tags = new HashSet<>();
    private boolean isActive = true;
    private LocalDate lastCompletedDate;
    private int currentStreak = 0;
    private int longestStreak = 0;
    private Integer targetStreak = null;


    // **COSTRUTTORE PACKAGE-PRIVATE PER IL BUILDER**
    public Habit(String name, String description, HabitFrequencyType frequency,
                 LocalDateTime createdAt, Set<String> tags,
                 HabitDifficulty difficulty) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdAt = createdAt;
        this.tags = tags != null ? new HashSet<>(tags) : new HashSet<>();
        this.difficulty = difficulty != null ? difficulty : HabitDifficulty.MEDIUM;
    }

    public Habit() {
        this.createdAt = LocalDateTime.now();
    }

    // **FACTORY METHOD PER IL BUILDER**
    public static HabitBuilder builder() {
        return new HabitBuilder();
    }


    public boolean isCompletedToday() {
        if (lastCompletedDate == null) {
            return false;
        }
        return lastCompletedDate.isEqual(LocalDate.now());
    }


    // **GETTERS E SETTERS**
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public HabitFrequencyType getFrequency() {
        return frequency;
    }

    public void setFrequency(HabitFrequencyType frequency) {
        this.frequency = frequency;
    }

    public HabitDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(HabitDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<String> getTags() {
        return new HashSet<>(tags);
    }

    public void setTags(Set<String> tags) {
        this.tags = tags != null ? new HashSet<>(tags) : new HashSet<>();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public LocalDate getLastCompletedDate() {
        return lastCompletedDate;
    }

    public void setLastCompletedDate(LocalDate lastCompletedDate) {
        this.lastCompletedDate = lastCompletedDate;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public Integer getTargetStreak() {
        return targetStreak;
    }

    public void setTargetStreak(Integer targetStreak) {
        this.targetStreak = targetStreak;
    }
}
