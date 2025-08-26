package ingsoftware.controller;

import ingsoftware.model.User;
import ingsoftware.service.StartupMediatorImpl;
import ingsoftware.service.UserService;
import ingsoftware.util.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
@FxmlView("/ingsoftware/MainDashboardView.fxml")
public class MainDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(MainDashboardController.class);

    // Dependencies
    private final FxWeaver fxWeaver;
    private final UserService userService;
    private final StartupMediatorImpl startupMediator;

    // Constructor injection
    public MainDashboardController(StartupMediatorImpl startupMediator, UserService userService, FxWeaver fxWeaver) {
        this.startupMediator = startupMediator;
        this.userService = userService;
        this.fxWeaver = fxWeaver;
    }

    public VBox userStats;
    public BorderPane habitList;

    @FXML
    private UserStatsController userStatsController;

    @FXML
    private HabitListController habitListController;

    private Long currentUserId;

    @FXML
    public void initialize() {
        checkAndCreateDefaultUser(currentUserId) ;
        Platform.runLater(() -> startupMediator.handleApplicationStartup(currentUserId));
    }

    public void setCurrentUser(Long userId) {
        this.currentUserId = userId;
        userStatsController.setCurrentUser(userId);
        habitListController.setCurrentUser(userId);
    }

    @FXML
    private void handleOpenEquipment() {
        try {
            Parent root = fxWeaver.loadView(EquipmentController.class);
            
            // Ottieni l'istanza del controller
            EquipmentController controller = fxWeaver.getBean(EquipmentController.class);
            
            // Imposta l'utente corrente nel controller dell'equipaggiamento
            controller.setCurrentUserId(currentUserId);
            
            Stage stage = new Stage();
            stage.setTitle("⚔️ Equipaggiamento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (Exception e) {
            logger.error("Errore durante l'apertura della finestra dell'equipaggiamento", e);
            showErrorMessage(e.getMessage());
        }
    }

    // Metodi per mostrare messaggi all'utente
    private void showErrorMessage(String message) {
        AlertHelper.showErrorAlert(message);
    }


    private void checkAndCreateDefaultUser(Long userId) {
        if (userService.checkDefaultUser()) {
            User defaultUser = new User();
            defaultUser.setLevel(1);
            defaultUser.setId(userId);
            userService.saveUser(defaultUser);
        }
    }
}

