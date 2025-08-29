package ingsoftware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launcher principale per l'applicazione Habit Tracker.
 * Questa classe separa la logica di avvio da JavaFX Application per evitare conflitti.
 */
public class HabitTrackerLauncher {
    
    private static final Logger log = LoggerFactory.getLogger(HabitTrackerLauncher.class);
    
    public static void main(String[] args) {
        try {
            log.info("🚀 Avvio Habit Tracker Launcher...");
            
            // Avvia l'applicazione principale
            HabitTrackerApplication.main(args);
            
        } catch (Exception e) {
            log.error("❌ Errore critico durante l'avvio: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}