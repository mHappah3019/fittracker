
package ingsoftware.service;

import ingsoftware.model.Equipment;
import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import ingsoftware.repository.EquipmentRepository;
import ingsoftware.repository.UserRepository;
import ingsoftware.repository.UserEquipmentRepository;
import jakarta.transaction.Transactional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserEquipmentRepository userEquipmentRepository;

    private final Map<EquipmentType, Equipment> activeEquipmentByTypeCache = new EnumMap<>(EquipmentType.class);


    public Map<EquipmentType, ObservableList<Equipment>> getAllEquipmentGroupedByType() {
        List<Equipment> availableEquipment = equipmentRepository.findByAvailableTrue();

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

    public void refreshCache() {
        activeEquipmentByTypeCache.clear();
        findAllEquippedByUser(userRepository.findById(1L).orElseThrow().getId()).forEach(
                (type, equipment) -> activeEquipmentByTypeCache.put(type, equipment)
        );
    }

    public Map<EquipmentType, Equipment> findAllEquippedByUser(Long userId) {
        List<UserEquipment> equippedUserEquipments = userEquipmentRepository.findByUserIdAndEquippedTrue(userId);

        // Utilizziamo uno Stream per processare la lista e gestire l'Optional in modo pulito.
        // Usiamo EnumMap per efficienza con chiavi enum.
        return equippedUserEquipments.stream()
                .map(UserEquipment::getEquipment) // Estrae l'equipaggiamento da UserEquipment
                .filter(e -> e.getType().isPresent()) // Filtra solo gli equipaggiamenti che hanno un tipo.
                .collect(Collectors.toMap(
                        e -> e.getType().get(), // Estrae il tipo dall'Optional per usarlo come chiave.
                        e -> e,                 // Usa l'equipaggiamento stesso come valore.
                        (existing, replacement) -> existing, // Se ci sono duplicati per tipo, mantiene il primo trovato.
                        () -> new EnumMap<>(EquipmentType.class) // Specifica che la mappa risultante deve essere un EnumMap.
                ));
    }

    @Transactional
    public Equipment equip(Long userId, Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipaggiamento non trovato"));

        // Verifica se l'utente possiede questo equipaggiamento
        UserEquipment userEquipment = userEquipmentRepository.findByUserIdAndEquipmentId(userId, equipmentId)
                .orElseThrow(() -> new RuntimeException("L'utente non possiede questo equipaggiamento"));

        // Rimuovi eventuali equipaggiamenti attivi dello stesso tipo
        EquipmentType equipmentType = equipment.getType()
                .orElseThrow(() -> new IllegalStateException("L'equipaggiamento deve avere un tipo per essere equipaggiato"));
        unequip(userId, equipmentType);

        // Equipaggia il nuovo equipaggiamento
        userEquipment.equip();
        userEquipmentRepository.save(userEquipment);
        
        return equipment;
    }

    @Transactional
    public void unequip(Long userId, EquipmentType type) {
        userEquipmentRepository.findEquippedByUserIdAndType(userId, type)
                .ifPresent(userEquipment -> {
                    userEquipment.unequip();
                    userEquipmentRepository.save(userEquipment);
                });
    }

    public Map<EquipmentType, Equipment> getFullEquipmentSet(Long userId) {
        return activeEquipmentByTypeCache;
    }

    public Optional<Equipment> findEquippedByUserAndType(Long currentUserId, EquipmentType type) {
        return userEquipmentRepository.findEquippedByUserIdAndType(currentUserId, type)
                .map(UserEquipment::getEquipment);
    }
    
    /**
     * Assegna un equipaggiamento a un utente (lo aggiunge al suo inventario)
     */
    @Transactional
    public UserEquipment assignEquipmentToUser(Long userId, Long equipmentId) {
        // Verifica se l'utente possiede già questo equipaggiamento
        if (userEquipmentRepository.existsByUserIdAndEquipmentId(userId, equipmentId)) {
            throw new RuntimeException("L'utente possiede già questo equipaggiamento");
        }
        
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipaggiamento non trovato"));
        
        UserEquipment userEquipment = new UserEquipment();
        userEquipment.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato")));
        userEquipment.setEquipment(equipment);
        
        return userEquipmentRepository.save(userEquipment);
    }
    
    /**
     * Ottiene tutti gli equipaggiamenti posseduti da un utente
     */
    public List<UserEquipment> getUserEquipments(Long userId) {
        return userEquipmentRepository.findByUserId(userId);
    }
    
    /**
     * Ottiene tutti gli equipaggiamenti equipaggiati da un utente
     */
    public List<UserEquipment> getEquippedItems(Long userId) {
        return userEquipmentRepository.findByUserIdAndEquippedTrue(userId);
    }
    
    /**
     * Verifica se un utente possiede un equipaggiamento specifico
     */
    public boolean userOwnsEquipment(Long userId, Long equipmentId) {
        return userEquipmentRepository.existsByUserIdAndEquipmentId(userId, equipmentId);
    }
}