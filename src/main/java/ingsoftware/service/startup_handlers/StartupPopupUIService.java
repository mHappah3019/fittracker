package ingsoftware.service.startup_handlers;

import ingsoftware.config.PopupConfig;
import ingsoftware.model.DTO.LifePointsDTO;
import ingsoftware.service.PopupUIService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.stereotype.Service;

@Service
public class StartupPopupUIService extends PopupUIService {

    public StartupPopupUIService(PopupConfig config) {
        super(config);
    }


    public void showfirstAccessPopup(LifePointsDTO result) {
        Platform.runLater(() -> {
            boolean isLevelDecreased = result.isLevelDecreased();
            Stage popup;

            if (isLevelDecreased) {
                popup = buildLevelDecreasedStage(result);
            }
            else {
                popup = buildFirstAccessStage(result);
            }
            animateAndShow(popup);

        });
    }


    private Stage buildFirstAccessStage(LifePointsDTO lifePointsDTO) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("popup-root");
        
        // Welcome message
        Label welcomeLabel = new Label("🎉 Benvenuto in FitTracker!");
        welcomeLabel.getStyleClass().add("welcome-label");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;");
        root.getChildren().add(welcomeLabel);
        
        // Life points information
        int oldLifePoints = lifePointsDTO.getOldLifePoints();
        int newLifePoints = lifePointsDTO.getNewlifePoints();
        Label lifePointsLabel = new Label( (oldLifePoints == newLifePoints)?
                ("💚 Punti Vita: " + lifePointsDTO.getOldLifePoints()) :
                ("💚 Punti Vita: " + lifePointsDTO.getOldLifePoints() + " → " + lifePointsDTO.getNewlifePoints()));
        lifePointsLabel.getStyleClass().add("life-points-label");
        lifePointsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50;");
        root.getChildren().add(lifePointsLabel);
        
        // Instructions
        Label instructionLabel = new Label("Inizia a tracciare le tue abitudini per guadagnare XP e migliorare!");
        instructionLabel.getStyleClass().add("instruction-label");
        instructionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666; -fx-text-alignment: center;");
        instructionLabel.setWrapText(true);
        instructionLabel.setMaxWidth(280);
        root.getChildren().add(instructionLabel);
        
        Scene scene = new Scene(root);
        
        // Try to load CSS, fallback to inline styles if not found
        java.net.URL cssResource = getClass().getResource("/ingsoftware/styles/style.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        } else {
            // Fallback styling
            root.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        }
        
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(Modality.NONE);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        
        // Position in center of screen
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setX((screen.getWidth() - 320) / 2);
        stage.setY((screen.getHeight() - 200) / 2);
        
        return stage;
    }

    private Stage buildLevelDecreasedStage(LifePointsDTO lifePointsDTO) {
        //generate a simple popup that tells the user that his level has decreased
        Stage stage = new Stage();
        stage.setTitle("FitTracker");
        stage.initModality(Modality.APPLICATION_MODAL);
        Label label = new Label("Il tuo livello è diminuito. Continua a tracciare le tue abitudini e a migliorarti.");
        stage.setScene(new Scene(label));
        return stage;
    }
}
