package ingsoftware;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

// Importa il controller che userai nella scena principale
// import ingsoftware.controller.HabitListController; // Non più necessario, useremo MainDashboardController
import ingsoftware.controller.MainDashboardController;

@SpringBootApplication // Assicurati che questo copra tutti i tuoi package
public class HabitTrackerApplication extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        // Avvia l'applicazione JavaFX
        launch(args);
    }

    @Override
    public void init() throws Exception {
        // Inizializza il contesto Spring prima di JavaFX
        // Passa la classe principale come sorgente di configurazione a Spring
        springContext = SpringApplication.run(HabitTrackerApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // LOG TUTTI I BEAN DISPONIBILI (utile per debugging)
            System.out.println("🔍 Bean Spring disponibili:");
            String[] beanNames = springContext.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                if (beanName.toLowerCase().contains("habit") ||
                        beanName.toLowerCase().contains("controller") ||
                        beanName.toLowerCase().contains("service") || // Aggiunto per debugging dei servizi
                        beanName.toLowerCase().contains("dao")) { // Aggiunto per debugging dei DAO
                    System.out.println("   - " + beanName);
                }
            }

            // CONTROLLA SPECIFICAMENTE IL CONTROLLER (utile per debugging)
            try {
                Object controller = springContext.getBean("mainDashboardController"); // Modificato da habitListController
                System.out.println("✅ MainDashboardController trovato: " + controller.getClass()); // Modificato
            } catch (Exception e) {
                System.out.println("❌ MainDashboardController NON trovato: " + e.getMessage()); // Modificato
            }

            // Carica la vista principale (Dashboard)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ingsoftware/MainDashboardView.fxml")); // Modificato
            // Imposta il controller factory per permettere a Spring di iniettare le dipendenze
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();

            // Configura la finestra principale
            Scene scene = new Scene(root, 1200, 800);
            //scene.getStylesheets().add(getClass().getResource("/ingsoftware/styles/styles.css").toExternalForm());

            primaryStage.setTitle("🎯 Habit Tracker - La Tua Avventura Quotidiana");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.centerOnScreen();
            primaryStage.show();

            // Passa l'ID utente al controller (per testing usa un ID fisso)
            MainDashboardController controller = loader.getController(); // Modificato il tipo di controller
            controller.setCurrentUser(1L); // ID utente di test

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento dell'interfaccia: " + e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        // Chiude il contesto Spring quando l'app viene chiusa
        springContext.close();
    }
}