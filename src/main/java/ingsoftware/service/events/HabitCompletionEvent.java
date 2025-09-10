package ingsoftware.service.events;

import ingsoftware.controller.strictly_view.HabitListViewManager;
import org.springframework.context.ApplicationEvent;

/**
 * Unified event for habit completion that can also carry UI display update payload.
 */
public class HabitCompletionEvent extends ApplicationEvent {

    private final Long habitId; // optional: the habit to refresh in UI
    private final HabitListViewManager.HabitListCell.DisplayMode displayMode; // optional UI mode

    // Minimal completion event, without UI payload
    public HabitCompletionEvent(Object source) {
        super(source);
        this.habitId = null;
        this.displayMode = null;
    }

    // Completion event that also requests a UI display update for a specific habit
    public HabitCompletionEvent(Object source, Long habitId, HabitListViewManager.HabitListCell.DisplayMode displayMode) {
        super(source);
        this.habitId = habitId;
        this.displayMode = displayMode;
    }

    public Long getHabitId() {
        return habitId;
    }

    public HabitListViewManager.HabitListCell.DisplayMode getDisplayMode() {
        return displayMode;
    }
}