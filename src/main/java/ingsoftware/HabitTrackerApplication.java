package ingsoftware;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

// Importa il controller che userai nella scena principale
import ingsoftware.controller.MainDashboardController;
import net.rgielen.fxweaver.core.FxWeaver;

@SpringBootApplication
public class HabitTrackerApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(HabitTrackerApplication.class);
    private static ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        try {
            log.info("🚀 Avvio Habit Tracker Application...");
            
            // Configura Spring Boot per applicazione desktop (non web)
            SpringApplication app = new SpringApplication(HabitTrackerApplication.class);
            app.setWebApplicationType(WebApplicationType.NONE);
            
            // Avvia prima Spring Boot, poi JavaFX
            springContext = app.run(args);
            log.info("✅ Contesto Spring Boot avviato con successo");
            
            // Avvia JavaFX
            Application.launch(HabitTrackerApplication.class, args);
            
        } catch (Exception e) {
            log.error("❌ Errore durante l'avvio dell'applicazione: {}", e.getMessage(), e);
            if (springContext != null) {
                springContext.close();
            }
            System.exit(1);
        }
    }

    @Override
    public void init() {
        // Il contesto Spring è già stato creato nel main
        if (springContext == null) {
            log.error("Spring context non inizializzato!");
            throw new RuntimeException("Spring context non disponibile");
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            log.info("🚀 Avvio dell'interfaccia JavaFX...");
            
            // Ottieni FxWeaver dal contesto Spring
            FxWeaver fxWeaver = springContext.getBean(FxWeaver.class);
            
            // LOG TUTTI I BEAN DISPONIBILI (utile per debugging)
            log.debug("🔍 Bean Spring disponibili:");
            String[] beanNames = springContext.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                if (beanName.toLowerCase().contains("habit") ||
                        beanName.toLowerCase().contains("controller") ||
                        beanName.toLowerCase().contains("service") ||
                        beanName.toLowerCase().contains("dao")) {
                    log.debug("   - {}", beanName);
                }
            }

            // Usa FxWeaver per caricare la vista principale
            Parent root = fxWeaver.loadView(MainDashboardController.class);
            MainDashboardController controller = fxWeaver.getBean(MainDashboardController.class);

            // Configura la finestra principale
           Scene scene = new Scene(root, 1200, 800);
            
            // Carica CSS se disponibile
            try {
                 scene.getStylesheets().add(getClass().getResource("/ingsoftware/styles/styles.css").toExternalForm());
            } catch (Exception e) {
                log.warn("File CSS non trovato, continuando senza stili personalizzati");
            }

            primaryStage.setTitle("🎯 Habit Tracker - La Tua Avventura Quotidiana");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.centerOnScreen();
            
            // Imposta l'utente corrente PRIMA di mostrare la finestra
            controller.setCurrentUser(1L); // ID utente di test
            
            primaryStage.show();
            log.info("✅ Interfaccia JavaFX avviata con successo");

        } catch (Exception e) {
            log.error("❌ Errore nel caricamento dell'interfaccia: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void stop() throws Exception {
        // Chiude il contesto Spring quando l'app viene chiusa
        springContext.close();
    }
}