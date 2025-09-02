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
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class EquipmentService {

    private final EquipmentDAO equipmentDAO;
    private final UserDAO userDAO;
    private final UserEquipmentDAO userEquipmentDAO;

    private final Map<EquipmentType, Equipment> activeEquipmentByTypeCache = new EnumMap<>(EquipmentType.class);

    public EquipmentService(EquipmentDAO equipmentDAO, UserDAO userDAO, UserEquipmentDAO userEquipmentDAO) {
        this.equipmentDAO = equipmentDAO;
        this.userDAO = userDAO;
        this.userEquipmentDAO = userEquipmentDAO;
    }


    /**
     * Retrieves all available equipment grouped by type, including "None" options for each type.
     * 
     * @return Map of equipment types to their corresponding observable lists of equipment
     */
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

    /**
     * Finds all equipment currently equipped by a specific user.
     * 
     * @param userId The ID of the user
     * @return Map of equipment types to their equipped equipment
     */
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

    /**
     * Finds equipment equipped by a user for a specific equipment type.
     * 
     * @param currentUserId The ID of the user
     * @param type The equipment type to search for
     * @return Optional containing the equipped equipment, if any
     */
    public Optional<Equipment> findEquippedByUserIdAndType(Long currentUserId, EquipmentType type) {
        return userEquipmentDAO.findEquippedByUserIdAndType(currentUserId, type)
                .flatMap(ue -> equipmentDAO.findById(ue.getEquipmentId()));
    }

    /**
     * Equips a specific equipment item for a user.
     * Automatically unequips any existing equipment of the same type.
     * 
     * @param userId The ID of the user
     * @param equipmentId The ID of the equipment to equip
     * @return The equipped Equipment object
     * @throws RuntimeException if equipment is not found or user doesn't own it
     */
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

    /**
     * Unequips equipment of a specific type for a user.
     * 
     * @param userId The ID of the user
     * @param type The type of equipment to unequip
     */
    @Transactional
    public void unequip(Long userId, EquipmentType type) {
        userEquipmentDAO.findEquippedByUserIdAndType(userId, type)
                .ifPresent(userEquipment -> {
                    userEquipment.unequip();
                    userEquipmentDAO.save(userEquipment);
                });
    }


    /**
     * Refreshes the equipment cache for a specific user.
     * 
     * @param userId The ID of the user whose cache should be refreshed
     */
    public void refreshCache(Long userId) {
        activeEquipmentByTypeCache.clear();
        activeEquipmentByTypeCache.putAll(findAllEquippedByUser(userId));
    }

    /**
     * Clears the equipment cache completely.
     * Useful for testing or when switching users.
     */
    public void clearCache() {
        activeEquipmentByTypeCache.clear();
    }

    /**
     * Gets the full equipment set for a user, using cache when possible.
     * 
     * @param userId The ID of the user
     * @return Map of equipment types to their equipped equipment
     */
    public Map<EquipmentType, Equipment> getFullEquipmentSet(Long userId) {
        if (activeEquipmentByTypeCache.isEmpty() && userId != null) {
            refreshCache(userId);
        }
        return activeEquipmentByTypeCache;
    }

    // ========================================
    // User Equipment Management Methods
    // ========================================

    /**
     * Initializes a user's equipment inventory by assigning all available equipment.
     * This method is useful during development or to simplify initial acquisition.
     * Should be called only once per user.
     *
     * @param userId The ID of the user to initialize
     * @throws RuntimeException if the user is not found
     */
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