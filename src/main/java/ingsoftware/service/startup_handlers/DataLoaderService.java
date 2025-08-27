package ingsoftware.service.startup_handlers;

import ingsoftware.dao.UserDAO;
import ingsoftware.model.User;
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

    private final UserDAO userDAO;
    private final EquipmentService equipmentService; // Inietta EquipmentService

    @Autowired
    public DataLoaderService(UserDAO userDAO, EquipmentService equipmentService) {
        this.userDAO = userDAO;
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
        if (userDAO.findAll().isEmpty()) {
            logger.info("Creazione utente di default...");

            User defaultUser = new User();
            defaultUser.setUsername("Player1");
            defaultUser.setLevel(1);
            defaultUser.setTotalXp(0);
            defaultUser.setLifePoints(100);
            defaultUser.setLastAccessDate(LocalDate.now());

            User savedUser = userDAO.save(defaultUser);
            logger.info("Utente di default creato con ID: {}", savedUser.getId());

            // Inizializza l'inventario dell'equipaggiamento per l'utente di default
            equipmentService.initializeUserEquipment(savedUser.getId());
            logger.info("Inventario equipaggiamento inizializzato per l'utente di default.");


        } else {
            logger.info("Utente di default già esistente, skip creazione.");

        }
    }

    /**
     * Metodo per ottenere l'utente di default (il primo utente nel database)
     */
    public User getDefaultUser() {
        return userDAO.findAll()
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