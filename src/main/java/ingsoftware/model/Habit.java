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

    // ========== FIELDS ==========
    
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

    // ========== CONSTRUCTORS ==========
    
    public Habit() {
        this.createdAt = LocalDateTime.now();
    }


    // ========== STATIC FACTORY METHODS ==========
    
    /**
     * Creates a new HabitBuilder instance for building Habit objects.
     */
    public static HabitBuilder builder() {
        return new HabitBuilder();
    }

    // ========== GETTERS ==========
    
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public HabitFrequencyType getFrequency() {
        return frequency;
    }

    public HabitDifficulty getDifficulty() {
        return difficulty;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Set<String> getTags() {
        return new HashSet<>(tags);
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDate getLastCompletedDate() {
        return lastCompletedDate;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public Integer getTargetStreak() {
        return targetStreak;
    }

    // ========== SETTERS ==========
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFrequency(HabitFrequencyType frequency) {
        this.frequency = frequency;
    }

    public void setDifficulty(HabitDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags != null ? new HashSet<>(tags) : new HashSet<>();
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setLastCompletedDate(LocalDate lastCompletedDate) {
        this.lastCompletedDate = lastCompletedDate;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public void setTargetStreak(Integer targetStreak) {
        this.targetStreak = targetStreak;
    }

    // ========== BUSINESS METHODS ==========
    
    /**
     * Checks if this habit was completed today.
     * 
     * @return true if completed today, false otherwise
     */
    public boolean isCompletedToday() {
        if (lastCompletedDate == null) {
            return false;
        }
        return lastCompletedDate.isEqual(LocalDate.now());
    }
}
