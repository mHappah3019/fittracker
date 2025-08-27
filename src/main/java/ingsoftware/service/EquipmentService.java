
package ingsoftware.service;

import ingsoftware.dao.EquipmentDAO;
import ingsoftware.dao.UserDAO;
import ingsoftware.dao.UserEquipmentDAO;
import ingsoftware.model.Equipment;
import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import jakarta.transaction.Transactional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

//TODO: riformattare per leggibilità

@Service
public class EquipmentService {

    @Autowired
    private EquipmentDAO equipmentDAO;

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private UserEquipmentDAO userEquipmentDAO;

    private final Map<EquipmentType, Equipment> activeEquipmentByTypeCache = new EnumMap<>(EquipmentType.class);


    public Map<EquipmentType, ObservableList<Equipment>> getAllEquipmentGroupedByType() {
        List<Equipment> availableEquipment = equipmentDAO.findByAvailableTrue();

        // Aggiungi l'opzione "NONE" per ogni tipo di equipaggiamento usando il nuovo metodo statico
        for (EquipmentType type : EquipmentType.values()) {
            Equipment noneEquipment = Equipment.createNoneOption();
            noneEquipment.setName("Nessun " + type.getDisplayName());
            noneEquipment.setType(type);
            availableEquipment.add(noneEquipment);
        }

        return availableEquipment.stream()
                .filter(e -> e.getType().isPresent()) // Assicura che l'equipaggiamento abbia un tipo
                .collect(Collectors.groupingBy(
                        e -> e.getType().get(),      // Raggruppa per EquipmentType
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                FXCollections::observableArrayList
                        )
                ));
    }

    public void refreshCache(Long userId) {
        activeEquipmentByTypeCache.clear();
        activeEquipmentByTypeCache.putAll(findAllEquippedByUser(userId));
    }


    public Map<EquipmentType, Equipment> findAllEquippedByUser(Long userId) {
        List<UserEquipment> equippedUserEquipments = userEquipmentDAO.findByUserIdAndEquippedTrue(userId);

        // Utilizziamo uno Stream per processare la lista e gestire l'Optional in modo pulito.
        // Usiamo EnumMap per efficienza con chiavi enum.
        return equippedUserEquipments.stream()
                .map(ue -> equipmentDAO.findById(ue.getEquipmentId()).orElse(null)) // Carica l'equipaggiamento tramite ID
                .filter(Objects::nonNull) // Filtra gli equipaggiamenti non trovati
                .filter(e -> e.getType().isPresent()) // Filtra solo gli equipaggiamenti che hanno un tipo.
                .collect(Collectors.toMap(
                        e -> e.getType().get(), // Estrae il tipo dall'Optional per usarlo come chiave.
                        e -> e,                 // Usa l'equipaggiamento stesso come valore.
                        (existing, _) -> existing, // Se ci sono duplicati per tipo, mantiene il primo trovato.
                        () -> new EnumMap<>(EquipmentType.class) // Specifica che la mappa risultante deve essere un EnumMap.
                ));
    }

    @Transactional
    public Equipment equip(Long userId, Long equipmentId) {
        Equipment equipment = equipmentDAO.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipaggiamento non trovato"));

        // Verifica se l'utente possiede questo equipaggiamento
        UserEquipment userEquipment = userEquipmentDAO.findByUserIdAndEquipmentId(userId, equipmentId)
                .orElseThrow(() -> new RuntimeException("L'utente non possiede questo equipaggiamento"));

        // Rimuovi eventuali equipaggiamenti attivi dello stesso tipo
        EquipmentType equipmentType = equipment.getType()
                .orElseThrow(() -> new IllegalStateException("L'equipaggiamento deve avere un tipo per essere equipaggiato"));
        unequip(userId, equipmentType);

        // Equipaggia il nuovo equipaggiamento
        userEquipment.equip();
        userEquipmentDAO.save(userEquipment);
        
        return equipment;
    }

    @Transactional
    public void unequip(Long userId, EquipmentType type) {
        userEquipmentDAO.findEquippedByUserIdAndType(userId, type)
                .ifPresent(userEquipment -> {
                    userEquipment.unequip();
                    userEquipmentDAO.save(userEquipment);
                });
    }

    public Map<EquipmentType, Equipment> getFullEquipmentSet(Long userId) {
        if (activeEquipmentByTypeCache.isEmpty() && userId != null) {
            refreshCache(userId);
        }
        return activeEquipmentByTypeCache;
    }

    public Optional<Equipment> findEquippedByUserAndType(Long currentUserId, EquipmentType type) {
        return userEquipmentDAO.findEquippedByUserIdAndType(currentUserId, type)
                .flatMap(ue -> equipmentDAO.findById(ue.getEquipmentId()));
    }

    /**
     * Inizializza l'inventario di un utente assegnandogli tutti gli equipaggiamenti disponibili.
     * Questo metodo è utile in fase di sviluppo o per semplificare l'acquisizione iniziale.
     * Deve essere chiamato una sola volta per utente.
     *
     * @param userId L'ID dell'utente da inizializzare.
     */
    @Transactional
    public void initializeUserEquipment(Long userId) {
        if (!userDAO.existsById(userId)) {
            throw new RuntimeException("Utente con ID " + userId + " non trovato.");
        }

        List<Equipment> allCatalogEquipment = equipmentDAO.findAll();

        for (Equipment equipment : allCatalogEquipment) {
            // Assegna l'equipaggiamento solo se l'utente non lo possiede già
            // Questo previene la creazione di duplicati se il metodo viene chiamato più volte
            if (!userEquipmentDAO.existsByUserIdAndEquipmentId(userId, equipment.getId())) {
                UserEquipment userEquipment = new UserEquipment(userId, equipment.getId());
                userEquipmentDAO.save(userEquipment);
            }
        }
    }


    
    /**
     * Ottiene tutti gli equipaggiamenti posseduti da un utente
     */
    public List<UserEquipment> getUserEquipments(Long userId) {
        return userEquipmentDAO.findByUserId(userId);
    }
    
    /**
     * Ottiene tutti gli equipaggiamenti equipaggiati da un utente
     */
    public List<UserEquipment> getEquippedItems(Long userId) {
        return userEquipmentDAO.findByUserIdAndEquippedTrue(userId);
    }
    
    /**
     * Verifica se un utente possiede un equipaggiamento specifico
     */
    public boolean userOwnsEquipment(Long userId, Long equipmentId) {
        return userEquipmentDAO.existsByUserIdAndEquipmentId(userId, equipmentId);
    }
}