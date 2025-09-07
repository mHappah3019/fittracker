package ingsoftware.controller.strictly_view;

import ingsoftware.model.Habit;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.List;

/**
 * Gestisce la lista delle abitudini nella vista principale.
 */
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

    public void updateHabitList(List<Habit> habits) {
        habitListView.getItems().setAll(habits);
    }



    /**
     * Aggiorna la visualizzazione di un'abitudine specifica usando solo l'ID.
     * Utile quando si ha solo l'ID dell'abitudine.
     */
    public void updateHabitDisplay(Long habitId, HabitListCell.DisplayMode displayMode) {
        if (habitId == null) return;
        
        // Trova l'abitudine nella lista usando l'ID
        habitListView.getItems().stream()
                .filter(h -> h.getId().equals(habitId))
                .findFirst()
                .ifPresent(foundHabit -> {
                    // Trova l'indice dell'abitudine nella lista
                    int index = habitListView.getItems().indexOf(foundHabit);
                    if (index >= 0) {
                        // Forza l'aggiornamento della cella specifica con i dati esistenti
                        refreshCellAt(index, foundHabit, displayMode);
                    }
                });
    }

    /**
     * Forza l'aggiornamento di una cella specifica.
     */
    private void refreshCellAt(int index, Habit updatedHabit, HabitListCell.DisplayMode displayMode) {
        // Aggiorna l'elemento nella lista con i dati freschi
        habitListView.getItems().set(index, updatedHabit);
        
        // Ottieni la cella visibile (se presente)
        HabitListCell cell = getCellAt(index);
        if (cell != null) {
            // Aggiorna direttamente la visualizzazione della cella
            cell.updateHabitDisplayPublic(updatedHabit, displayMode);
        }
    }

    /**
     * Ottiene la cella a un indice specifico, se visibile.
     */
    private HabitListCell getCellAt(int index) {
        // Questo metodo funziona solo per le celle attualmente visibili
        return (HabitListCell) habitListView.lookupAll(".list-cell")
                .stream()
                .filter(node -> node instanceof HabitListCell)
                .map(node -> (HabitListCell) node)
                .filter(cell -> cell.getIndex() == index)
                .findFirst()
                .orElse(null);
    }

    public static class HabitListCell extends ListCell<Habit> {
        
        public enum DisplayMode {
            FULL,        // Mostra nome e streak
            NAME_ONLY,   // Mostra solo il nome
            STREAK_ONLY  // Aggiorna solo la streak
        }
        
        private PauseTransition streakAnimation;
        private HBox container;
        private Label nameLabel;
        private Label streakLabel;
        
        public HabitListCell() {
            // Crea il layout container
            container = new HBox();
            container.setAlignment(Pos.CENTER_LEFT);
            
            // Label per il nome dell'abitudine (a sinistra)
            nameLabel = new Label();
            
            // Spacer per spingere la streak a destra
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            // Label per la streak (a destra)
            streakLabel = new Label();
            streakLabel.setAlignment(Pos.CENTER_RIGHT);
            
            // Aggiungi tutti gli elementi al container
            container.getChildren().addAll(nameLabel, spacer, streakLabel);
        }
        
        @Override
        protected void updateItem(Habit habit, boolean empty) {
            super.updateItem(habit, empty);

            // Cancella l'animazione precedente se esiste
            stopStreakAnimation();

            if (empty || habit == null) {
                clearCell();
            } else {
                updateHabitDisplay(habit, DisplayMode.NAME_ONLY);
            }
        }
        
        private void clearCell() {
            setText(null);
            setGraphic(null);
            setStyle("");
        }
        
        private void updateHabitDisplay(Habit habit, DisplayMode mode) {
            updateHabitName(habit);
            
            switch (mode) {
                case FULL -> updateStreakDisplay(habit);
                case NAME_ONLY -> hideStreak();
                case STREAK_ONLY -> {
                    // Mantieni il nome esistente, aggiorna solo la streak
                    updateStreakDisplay(habit);
                }
            }
            
            // Usa il container come grafica invece del testo
            setText(null);
            setGraphic(container);
        }

        /**
         * Metodo pubblico per aggiornare la visualizzazione della cella.
         * Utilizzato dal HabitListViewManager per aggiornamenti mirati.
         */
        public void updateHabitDisplayPublic(Habit habit, DisplayMode mode) {
            updateHabitDisplay(habit, mode);
        }
        
        private void updateHabitName(Habit habit) {
            nameLabel.setText(habit.getName());
            
            // Stile basato sullo stato (solo per il testo, non per lo sfondo)
            String textStyle = habit.isCompletedToday() 
                ? "-fx-text-fill: #4CAF50; -fx-font-weight: bold;"
                : "-fx-text-fill: #333333;";
            nameLabel.setStyle(textStyle);
        }
        
        private void updateStreakDisplay(Habit habit) {
            int currentStreak = habit.getCurrentStreak();
            
            if (currentStreak > 1) {
                showStreakWithAnimation(currentStreak);
            } else {
                hideStreak();
            }
        }
        
        private void showStreakWithAnimation(int streakCount) {
            // Mostra la streak a destra
            streakLabel.setText("🔥 " + streakCount + " giorni");
            streakLabel.setStyle("-fx-text-fill: #FF6B35; -fx-font-size: 12px;");
            streakLabel.setVisible(true);
            
            // Animazione che nasconde l'indicatore dopo 5 secondi
            streakAnimation = new PauseTransition(Duration.seconds(5));
            streakAnimation.setOnFinished(e -> hideStreak());
            streakAnimation.play();
        }
        
        private void hideStreak() {
            streakLabel.setVisible(false);
        }
        
        private void stopStreakAnimation() {
            if (streakAnimation != null) {
                streakAnimation.stop();
            }
        }
    }
}
