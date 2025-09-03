package ingsoftware.controller;

import ingsoftware.exception.BusinessException;
import ingsoftware.model.User;
import ingsoftware.service.UserService;
import ingsoftware.service.events.HabitCompletionEvent;
import ingsoftware.util.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
public class UserStatsController {

    private static final double XP_PER_LEVEL = 100.0;

    private static final Logger logger = LoggerFactory.getLogger(UserStatsController.class);

    private final UserService userService;

    // Constructor injection
    public UserStatsController(UserService userService) {
        this.userService = userService;
    }

    // Componenti FXML
    @FXML private Label levelLabel;
    @FXML private ProgressBar experienceBar;
    @FXML private Label experienceLabel;
    @FXML private ProgressBar healthBar;
    @FXML private Label healthLabel;

    private Long currentUserId;

    // FXML initialization method - waits for user to be set
    @FXML
    public void initialize() {
        // Inizializzazione vuota, aspetta setCurrentUser
    }

    // Sets the current user and refreshes their stats display
    public void setCurrentUser(Long userId) {
        this.currentUserId = userId;
        updateUserStats();
    }

    // Listens for habit completion events and updates stats on JavaFX thread
    @EventListener(HabitCompletionEvent.class)
    public void handleHabitCompletionEvent() {
        Platform.runLater(
                this::updateUserStats);
    }

    // Fetches user data and updates the UI components with current stats
    public void updateUserStats() {
        if (currentUserId == null) {
            logger.warn("Tentativo di aggiornare le statistiche senza un utente corrente impostato");
            return;
        }

        try {

            User user = userService.findUserOrThrow(currentUserId);
            updateUIWithUserData(user);

        } catch (BusinessException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Errore imprevisto durante l'aggiornamento delle statistiche per l'utente {}", currentUserId, e);
            showErrorMessage("Impossibile caricare le statistiche dell'utente. Riprova più tardi.");
        }
    }
    
    // Updates all UI elements with the user's current level, health, and XP data
    private void updateUIWithUserData(User user) {

        // Aggiorna il livello
        levelLabel.setText(String.valueOf(user.getLevel()));

        // Aggiorna i punti vita
        int lifePoints = user.getLifePoints();
        int maxLifePoints = 100; // Valore massimo dei punti vita
        healthBar.setProgress((double) lifePoints / maxLifePoints);
        healthLabel.setText(lifePoints + "/" + maxLifePoints);

        // Aggiorna l'esperienza
        double xp = user.getTotalXp();
        double xpForNextLevel = calculateXpForNextLevel(user.getLevel());
        //experienceBar.setProgress(xp  / xpForNextLevel);
        experienceBar.setProgress( ( (xp - XP_PER_LEVEL*(user.getLevel()-1))) / XP_PER_LEVEL);
        experienceLabel.setText((int)xp + "/" + (int)xpForNextLevel);
    }

    // Calculates the total XP required to reach the next level
    private double calculateXpForNextLevel(int currentLevel) {
        return XP_PER_LEVEL * currentLevel;
    }
    
    // Shows error messages to the user via alert dialogs
    private void showErrorMessage(String message) {
        AlertHelper.showErrorAlert(message);
    }
}

