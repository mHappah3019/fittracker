package ingsoftware.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "habit_completions")
public class HabitCompletion {

    // ========== FIELDS ==========
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "habit_id", nullable = false)
    private Long habitId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private LocalDate completionDate;
    private int streak;
    private String notes;

    // ========== CONSTRUCTORS ==========
    
    public HabitCompletion() {
        this.completionDate = LocalDate.now();
    }

    // ========== GETTERS ==========
    
    public Long getId() {
        return id;
    }

    public Long getHabitId() {
        return habitId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public int getStreak() {
        return streak;
    }

    public String getNotes() {
        return notes;
    }

    // ========== SETTERS ==========
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ========== BUSINESS METHODS ==========
    
    /**
     * Checks if this completion was made today.
     */
    public boolean isCompletedToday() {
        return completionDate != null && completionDate.isEqual(LocalDate.now());
    }

    /**
     * Checks if this completion has notes.
     */
    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }
}
