package ingsoftware.service.startup_handlers;

import ingsoftware.model.User;
import ingsoftware.repository.UserRepository;
import ingsoftware.service.EquipmentService; // Importa EquipmentService
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DataLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderService.class);

    private final UserRepository userRepository;
    private final EquipmentService equipmentService; // Inietta EquipmentService

    @Autowired
    public DataLoaderService(UserRepository userRepository, EquipmentService equipmentService) {
        this.userRepository = userRepository;
        this.equipmentService = equipmentService; // Assegna EquipmentService
    }

    @PostConstruct
    @Transactional
    public void initializeDefaultData() {
        logger.info("Inizializzazione dati di default...");

        // Crea utente di default se non esiste
        createDefaultUserIfNotExists();

        logger.info("Inizializzazione dati completata.");
    }

    private void createDefaultUserIfNotExists() {
        if (userRepository.count() == 0) {
            logger.info("Creazione utente di default...");

            User defaultUser = new User();
            defaultUser.setUsername("Player1");
            defaultUser.setLevel(1);
            defaultUser.setTotalXp(0);
            defaultUser.setLifePoints(100);
            defaultUser.setLastAccessDate(LocalDate.now());

            User savedUser = userRepository.save(defaultUser);
            logger.info("Utente di default creato con ID: {}", savedUser.getId());

            // Inizializza l'inventario dell'equipaggiamento per l'utente di default
            equipmentService.initializeUserEquipment(savedUser.getId());
            logger.info("Inventario equipaggiamento inizializzato per l'utente di default.");


        } else {
            logger.info("Utente di default già esistente, skip creazione.");
            // Potresti anche qui voler inizializzare l'equipaggiamento per l'utente esistente
            // se non sei sicuro che sia già stato fatto, o se hai aggiunto nuovi equipaggiamenti al catalogo.
            // Però, attenzione a non sovrascrivere lo stato di "equipaggiato" se non desiderato.
            // In fase di sviluppo, potrebbe essere utile chiamarlo ogni volta, ma in produzione no.
            // Long defaultUserId = getDefaultUserId();
            // equipmentService.initializeUserEquipment(defaultUserId); // <--- DA CONSIDERARE CON ATTENZIONE
        }
    }

    /**
     * Metodo per ottenere l'utente di default (il primo utente nel database)
     */
    public User getDefaultUser() {
        return userRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nessun utente di default trovato"));
    }

    /**
     * Metodo per ottenere l'ID dell'utente di default
     */
    public Long getDefaultUserId() {
        return getDefaultUser().getId();
    }
}