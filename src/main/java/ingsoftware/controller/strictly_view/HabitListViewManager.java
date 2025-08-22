package ingsoftware.controller.strictly_view;

import ingsoftware.model.Habit;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.List;

public class HabitListViewManager {

    private final ListView<Habit> habitListView;
    private final Button editHabitButton;
    private final Button completeHabitButton;

    public HabitListViewManager(
            ListView<Habit> habitListView,
            Button editHabitButton,
            Button completeHabitButton) {
        this.habitListView = habitListView;
        this.editHabitButton = editHabitButton;
        this.completeHabitButton = completeHabitButton;

        setupListView();
        setupSelectionHandlers();
    }

    public void setupListView() {
        habitListView.setCellFactory(_ -> new HabitListCell());
    }

    public void setupDoubleClickHandler(Runnable onDoubleClick) {
        habitListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                onDoubleClick.run();
            }
        });
    }

    public void setupSelectionHandlers() {
        habitListView.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newSelection) -> {
                    boolean hasSelection = newSelection != null;
                    editHabitButton.setDisable(!hasSelection);
                    completeHabitButton.setDisable(!hasSelection);
                }
        );
    }

    public Habit getSelectedHabit() {
        return habitListView.getSelectionModel().getSelectedItem();
    }


    /**
     * Updates the habit list with the provided habits.
     * The responsibility of fetching habits is now delegated to the calling controller.
     */
    public void updateHabitList(List<Habit> habits) {
        habitListView.getItems().setAll(habits);
    }

    /**
     * Classe interna per la visualizzazione personalizzata delle abitudini nella ListView.
     * Essendo strettamente correlata alla gestione della vista delle abitudini,
     * è logico mantenerla all'interno del manager.
     */
    static class HabitListCell extends ListCell<Habit> {
        @Override
        protected void updateItem(Habit habit, boolean empty) {
            super.updateItem(habit, empty);

            if (empty || habit == null) {
                setText(null);
                setStyle("");
            } else {
                setText(habit.getName());
                // Stile basato sullo stato
                if (habit.isCompletedToday()) {
                    setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                } else {
                    setStyle("-fx-text-fill: #333333;");
                }
            }
        }
    }
}
