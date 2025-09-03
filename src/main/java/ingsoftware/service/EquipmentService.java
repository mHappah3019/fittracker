package ingsoftware.service;

import ingsoftware.dao.EquipmentDAO;
import ingsoftware.dao.UserDAO;
import ingsoftware.dao.UserEquipmentDAO;
import ingsoftware.model.Equipment;
import ingsoftware.model.User;
import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import jakarta.transaction.Transactional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class EquipmentService {

    private final EquipmentDAO equipmentDAO;
    private final UserDAO userDAO;
    private final UserEquipmentDAO userEquipmentDAO;

    public EquipmentService(EquipmentDAO equipmentDAO, UserDAO userDAO, UserEquipmentDAO userEquipmentDAO) {
        this.equipmentDAO = equipmentDAO;
        this.userDAO = userDAO;
        this.userEquipmentDAO = userEquipmentDAO;
    }


    // Retrieves all available equipment grouped by type, including "None" options for each type
    // Returns observable lists suitable for JavaFX UI components
    public Map<EquipmentType, ObservableList<Equipment>> getAllEquipmentGroupedByType() {
        List<Equipment> availableEquipment = new ArrayList<>(equipmentDAO.findByAvailableTrue());

        // Add "NONE" option for each equipment type using the static method
        for (EquipmentType type : EquipmentType.values()) {
            Equipment noneEquipment = Equipment.createNoneOption();
            noneEquipment.setName("Nessun " + type.getDisplayName());
            noneEquipment.setType(type);
            availableEquipment.add(noneEquipment);
        }

        return availableEquipment.stream()
                .filter(e -> e.getType().isPresent()) // Ensure equipment has a type
                .collect(Collectors.groupingBy(
                        e -> e.getType().get(),      // Group by EquipmentType
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                FXCollections::observableArrayList
                        )
                ));
    }

    // Finds all equipment currently equipped by a specific user
    // Returns map of equipment types to their equipped equipment
    public Map<EquipmentType, Equipment> findAllEquippedByUser(Long userId) {
        List<UserEquipment> equippedUserEquipments = userEquipmentDAO.findByUserIdAndEquippedTrue(userId);

        // Use Stream to process the list and handle Optional cleanly
        // Use EnumMap for efficiency with enum keys
        return equippedUserEquipments.stream()
                .map(ue -> equipmentDAO.findById(ue.getEquipmentId()).orElse(null)) // Load equipment by ID
                .filter(Objects::nonNull) // Filter out equipment not found
                .filter(e -> e.getType().isPresent()) // Filter only equipment that has a type
                .collect(Collectors.toMap(
                        e -> e.getType().get(), // Extract type from Optional to use as key
                        e -> e,                 // Use equipment itself as value
                        (existing, _) -> existing, // If duplicates by type, keep the first found
                        () -> new EnumMap<>(EquipmentType.class) // Specify result map as EnumMap
                ));
    }

    // Finds equipment equipped by a user for a specific equipment type
    // Returns Optional containing the equipped equipment, if any
    public Optional<Equipment> findEquippedByUserIdAndType(Long currentUserId, EquipmentType type) {
        return userEquipmentDAO.findEquippedByUserIdAndType(currentUserId, type)
                .flatMap(ue -> equipmentDAO.findById(ue.getEquipmentId()));
    }

    // Equips a specific equipment item for a user, automatically unequipping same type
    // Validates user ownership and throws RuntimeException if equipment not found or not owned
    @Transactional
    public Equipment equip(Long userId, Long equipmentId) {
        Equipment equipment = equipmentDAO.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipaggiamento non trovato"));

        // Verify that the user owns this equipment
        UserEquipment userEquipment = userEquipmentDAO.findByUserIdAndEquipmentId(userId, equipmentId)
                .orElseThrow(() -> new RuntimeException("L'utente non possiede questo equipaggiamento"));

        // Remove any active equipment of the same type
        EquipmentType equipmentType = equipment.getType()
                .orElseThrow(() -> new IllegalStateException("L'equipaggiamento deve avere un tipo per essere equipaggiato"));
        unequip(userId, equipmentType);

        // Equip the new equipment
        userEquipment.equip();
        userEquipmentDAO.save(userEquipment);
        
        return equipment;
    }

    // Unequips equipment of a specific type for a user
    // Does nothing if no equipment of that type is currently equipped
    @Transactional
    public void unequip(Long userId, EquipmentType type) {
        userEquipmentDAO.findEquippedByUserIdAndType(userId, type)
                .ifPresent(userEquipment -> {
                    userEquipment.unequip();
                    userEquipmentDAO.save(userEquipment);
                });
    }



    // Gets the full equipment set for a user as a list of Equipment objects
    // Performs join between UserEquipment and Equipment tables for currently equipped items
    public List<Equipment> getFullEquipmentSet(Long userId) {
        List<UserEquipment> equippedUserEquipments = userEquipmentDAO.findByUserIdAndEquippedTrue(userId);
        
        return equippedUserEquipments.stream()
                .map(ue -> equipmentDAO.findById(ue.getEquipmentId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ========================================
    // User Equipment Management Methods
    // ========================================

    // Initializes a user's equipment inventory by assigning all available equipment
    // Useful for development or initial setup, should be called only once per user
    @Transactional
    public void initializeUserEquipment(Long userId) {
        if (!userDAO.existsById(userId)) {
            throw new RuntimeException("Utente con ID " + userId + " non trovato.");
        }

        List<Equipment> allCatalogEquipment = equipmentDAO.findAll();

        for (Equipment equipment : allCatalogEquipment) {
            // Assign equipment only if the user doesn't already own it
            // This prevents duplicate creation if the method is called multiple times
            if (!userEquipmentDAO.existsByUserIdAndEquipmentId(userId, equipment.getId())) {
                UserEquipment userEquipment = new UserEquipment(userId, equipment.getId());
                userEquipmentDAO.save(userEquipment);
            }
        }
    }
}