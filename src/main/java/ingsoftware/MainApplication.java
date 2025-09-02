package ingsoftware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// DEPRECATO: Questa classe è stata sostituita da HabitTrackerApplication
// che gestisce correttamente l'integrazione Spring Boot + JavaFX
/*
@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
*/

// Classe di backup per test senza JavaFX
public class MainApplication {
    public static void main(String[] args) {
        System.out.println("⚠️ ATTENZIONE: Stai usando MainApplication invece di HabitTrackerApplication");
        System.out.println("💡 Per l'applicazione completa con JavaFX, usa: ingsoftware.HabitTrackerApplication");
        SpringApplication.run(MainApplication.class, args);
    }
}

