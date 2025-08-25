package ingsoftware.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "habit_completions")
public class HabitCompletion {
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

    // Costruttore di default
    public HabitCompletion() {
        this.completionDate = LocalDate.now();
    }

    // BUSINESS LOGIC METHODS
    public boolean isCompletedToday() {
        return completionDate != null && completionDate.isEqual(LocalDate.now());
    }

    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }

    // FLUENT INTERFACE per configurazione post-costruzione
    public HabitCompletion withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    // GETTERS E SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHabitId() { return habitId; }
    public void setHabitId(Long habitId) { this.habitId = habitId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public void setStreak(int streak) { this.streak = streak; }
    public int getStreak() { return streak; }
}
