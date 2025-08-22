package ingsoftware.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class HabitCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habit;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDate completionDate;
    private int streak;
    private String notes;


    // **COSTRUTTORI**
    public HabitCompletion() {
        this.completionDate = LocalDate.now();
    }

    // **BUSINESS LOGIC METHODS**

    public boolean isCompletedToday() {
        return completionDate.isEqual(LocalDate.now());
    }

    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }

    // **FLUENT INTERFACE per configurazione post-costruzione**
    public HabitCompletion withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    // **GETTERS E SETTERS**
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Habit getHabit() { return habit; }
    public void setHabit(Habit habit) { this.habit = habit; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public void setStreak(int streak) { this.streak = streak; }
    public int getStreak() { return streak; }
}
