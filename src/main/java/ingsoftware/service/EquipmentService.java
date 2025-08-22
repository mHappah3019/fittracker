
package ingsoftware.service;

import ingsoftware.model.Equipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import ingsoftware.model.Equipment.EquipmentState;
import ingsoftware.repository.EquipmentRepository;
import ingsoftware.repository.UserRepository;
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

    private final Map<EquipmentType, Equipment> activeEquipmentByTypeCache = new EnumMap<>(EquipmentType.class);


    public Map<EquipmentType, ObservableList<Equipment>> getAllEquipmentGroupedByType() {
        List<Equipment> allEquipment = equipmentRepository.findAll();

        // Aggiungi l'opzione "NONE" per ogni tipo di equipaggiamento usando il nuovo metodo statico
        for (EquipmentType type : EquipmentType.values()) {
            Equipment noneEquipment = Equipment.createNoneOption();
            noneEquipment.setName("Nessun " + type.getDisplayName());
            noneEquipment.setType(type);
            allEquipment.add(noneEquipment);
        }

        return allEquipment.stream()
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
        List<Equipment> equipped = equipmentRepository.findByStateEquals(Equipment.EquipmentState.ACTIVE);

        // Utilizziamo uno Stream per processare la lista e gestire l'Optional in modo pulito.
        // Usiamo EnumMap per efficienza con chiavi enum.
        return equipped.stream()
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

        // Rimuovi eventuali equipaggiamenti attivi dello stesso tipo
        unequip(userId, equipment.getType().orElseThrow(() -> new IllegalStateException("L'equipaggiamento deve avere un tipo per essere equipaggiato")));

        equipment.setState(EquipmentState.ACTIVE);
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public void unequip(Long userId, EquipmentType type) {
        equipmentRepository.findByTypeAndState(type, EquipmentState.ACTIVE)
                .ifPresent(equipment -> {
                    equipment.setState(EquipmentState.INACTIVE);
                    equipmentRepository.save(equipment);
                });
    }

    public Map<EquipmentType, Equipment> getFullEquipmentSet(Long userId) {
        return activeEquipmentByTypeCache;
    }

    public Optional<Equipment> findEquippedByUserAndType(Long currentUserId, EquipmentType type) {
        return Optional.ofNullable(activeEquipmentByTypeCache.get(type));
    }
}