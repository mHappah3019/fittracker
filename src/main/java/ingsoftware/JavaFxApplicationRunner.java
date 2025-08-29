package ingsoftware;

import javafx.application.Application;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// DISABILITATO: Questo componente causava conflitti nell'avvio
// L'avvio di JavaFX è ora gestito direttamente da HabitTrackerApplication.main()
/*
@Component
public class JavaFxApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        // Avvia JavaFX in un thread separato
        new Thread(() -> Application.launch(HabitTrackerApplication.class)).start();
    }
}
*/
