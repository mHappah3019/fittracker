package ingsoftware.service.startup_handlers;

import ingsoftware.config.PopupConfig;
import ingsoftware.model.DTO.LifePointsDTO;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StartupPopupUIService {
    
    @Autowired
    private PopupConfig config;
    
    public void showPopup(String s, String s1) {

    }

    public void showPopup(LifePointsDTO result) {
    }

    public void showfirstAccessPopup(LifePointsDTO result) {
        Platform.runLater(() -> {
            Stage popup = buildFirstAccessStage(result);
            animateAndShow(popup);
        });
    }

    //TODO: modifica per mostrare aumento/decremento di vita e messaggio personalizzato se lifePointsDTO.isLevelDecreased() == true
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
        Label lifePointsLabel = new Label("💚 Punti Vita: " + lifePointsDTO.getNewlifePoints());
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
        java.net.URL cssResource = getClass().getResource("/ingsoftware/styles/styles.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        } else {
            // Fallback styling
            root.setStyle("-fx-background-color: white; -fx-border-color: #2E8B57; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
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
    
    private void animateAndShow(Stage stage) {
        FadeTransition fadeIn = new FadeTransition(config.getFadeIn(), stage.getScene().getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        PauseTransition hold = new PauseTransition(config.getDisplay().multiply(2)); // Show longer for first access
        
        FadeTransition fadeOut = new FadeTransition(config.getFadeOut(), stage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> stage.close());
        
        SequentialTransition seq = new SequentialTransition(fadeIn, hold, fadeOut);
        stage.show();
        seq.play();
    }
}
