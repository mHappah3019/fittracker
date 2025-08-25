package ingsoftware.model.builder;

import ingsoftware.model.HabitCompletion;

import java.time.LocalDate;

public class HabitCompletionBuilder {
    private Long habitId;
    private Long userId;
    private LocalDate completionDate;
    private Integer streak;
    private String notes;

    public HabitCompletionBuilder() {}

    public HabitCompletionBuilder withHabitId(Long habitId) {
        this.habitId = habitId;
        return this;
    }

    public HabitCompletionBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public HabitCompletionBuilder withCompletionDate(LocalDate date) {
        this.completionDate = date;
        return this;
    }

    public HabitCompletionBuilder withStreak(int streak) {
        this.streak = streak;
        return this;
    }

    public HabitCompletionBuilder withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public HabitCompletion build() throws IllegalArgumentException {
        validate();

        HabitCompletion completion = new HabitCompletion();
        completion.setHabitId(habitId);
        completion.setUserId(userId);
        completion.setCompletionDate(completionDate != null ? completionDate : LocalDate.now());

        if (streak != null) {
            completion.setStreak(streak);
        }

        if (notes != null) {
            completion.setNotes(notes);
        }

        return completion;
    }

    private void validate() {
        if (habitId == null) {
            throw new IllegalArgumentException("L'abitudine (habitId) è obbligatoria");
        }
        if (userId == null) {
            throw new IllegalArgumentException("L'utente (userId) è obbligatorio");
        }
        if (completionDate == null) {
            throw new IllegalArgumentException("La data di completamento è obbligatoria");
        }

        if (completionDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La data di completamento non può essere nel futuro");
        }

        if (streak != null && streak < 0) {
            throw new IllegalArgumentException("Lo streak non può essere negativo");
        }

        if (notes != null && notes.length() > 500) {
            throw new IllegalArgumentException("Le note non possono superare i 500 caratteri");
        }
    }
}