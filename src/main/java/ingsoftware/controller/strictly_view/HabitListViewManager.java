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

    static class HabitListCell extends ListCell<Habit> {
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
            if (streakAnimation != null) {
                streakAnimation.stop();
            }

            if (empty || habit == null) {
                setText(null);
                setGraphic(null);
                setStyle("");
            } else {
                // Imposta il nome dell'abitudine
                nameLabel.setText(habit.getName());
                
                // Stile basato sullo stato (solo per il testo, non per lo sfondo)
                String textStyle;
                if (habit.isCompletedToday()) {
                    textStyle = "-fx-text-fill: #4CAF50; -fx-font-weight: bold;";
                } else {
                    textStyle = "-fx-text-fill: #333333;";
                }
                nameLabel.setStyle(textStyle);
                
                // Gestisci la visualizzazione della streak
                int currentStreak = habit.getCurrentStreak();
                if (currentStreak > 0) {
                    // Mostra la streak a destra
                    streakLabel.setText("🔥 " + currentStreak + " giorni");
                    streakLabel.setStyle("-fx-text-fill: #FF6B35; -fx-font-size: 12px;");
                    streakLabel.setVisible(true);
                    
                    // Animazione che nasconde l'indicatore dopo 5 secondi
                    streakAnimation = new PauseTransition(Duration.seconds(5));
                    streakAnimation.setOnFinished(e -> {
                        streakLabel.setVisible(false);
                    });
                    streakAnimation.play();
                } else {
                    streakLabel.setVisible(false);
                }
                
                // Usa il container come grafica invece del testo
                setText(null);
                setGraphic(container);
            }
        }
    }
}
