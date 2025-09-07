package ingsoftware.service.events;

import ingsoftware.controller.strictly_view.HabitListViewManager;
import ingsoftware.model.Habit;
import org.springframework.context.ApplicationEvent;

/**
 * Evento per richiedere l'aggiornamento della visualizzazione di un'abitudine specifica.
 * Utilizzato per mostrare animazioni di streak senza ricaricare tutta la lista.
 */
public class HabitDisplayUpdateEvent extends ApplicationEvent {

    private final Habit habit;
    private final Long habitId;
    private final HabitListViewManager.HabitListCell.DisplayMode displayMode;

    /**
     * Costruttore per aggiornamento con solo ID dell'abitudine.
     */
    public HabitDisplayUpdateEvent(Object source, Long habitId, HabitListViewManager.HabitListCell.DisplayMode displayMode) {
        super(source);
        this.habit = null;
        this.habitId = habitId;
        this.displayMode = displayMode;
    }

    public Habit getHabit() {
        return habit;
    }

    public Long getHabitId() {
        return habitId;
    }

    public HabitListViewManager.HabitListCell.DisplayMode getDisplayMode() {
        return displayMode;
    }

    /**
     * Indica se l'evento contiene l'oggetto Habit completo o solo l'ID.
     */
    public boolean hasFullHabit() {
        return habit != null;
    }
}