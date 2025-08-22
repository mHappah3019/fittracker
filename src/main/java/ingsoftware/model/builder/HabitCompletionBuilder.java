package ingsoftware.model.builder;

import ingsoftware.model.Habit;
import ingsoftware.model.HabitCompletion;
import ingsoftware.model.User;

import java.time.LocalDate;

public class HabitCompletionBuilder {
    private Habit habit;
    private User user;
    private LocalDate completionDate;
    private Integer streak;
    private String notes;

    public HabitCompletionBuilder() {}

    public HabitCompletionBuilder withHabit(Habit habit) {
        this.habit = habit;
        return this;
    }

    public HabitCompletionBuilder withUser(User user) {
        this.user = user;
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
        completion.setHabit(habit);
        completion.setUser(user);
        completion.setCompletionDate(completionDate != null ? completionDate : LocalDate.now());

        // Solo se lo streak è stato esplicitamente impostato
        if (streak != null) {
            completion.setStreak(streak);
        }

        if (notes != null) {
            completion.setNotes(notes);
        }

        return completion;
    }

    private void validate() {
        if (habit == null) {
            throw new IllegalArgumentException("L'abitudine è obbligatoria");
        }
        if (user == null) {
            throw new IllegalArgumentException("L'utente è obbligatorio");
        }
        if (completionDate == null) {
            throw new IllegalArgumentException("La data di completamento è obbligatoria");
        }

        // Validazioni business logic
        if (completionDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La data di completamento non può essere nel futuro");
        }

        if (streak < 0) {
            throw new IllegalArgumentException("Lo streak non può essere negativo");
        }

        if (notes != null && notes.length() > 500) {
            throw new IllegalArgumentException("Le note non possono superare i 500 caratteri");
        }
    }
}