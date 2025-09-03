package ingsoftware.dao;

import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface for UserEquipment entity operations.
 * Manages the relationship between users and their owned/equipped items.
 */
public interface UserEquipmentDAO extends BaseDAO<UserEquipment, Long> {
    // Retrieves all equipment owned by a specific user
    List<UserEquipment> findByUserId(Long userId);
    
    // Finds only the equipments that a user currently has equipped
    List<UserEquipment> findByUserIdAndEquippedTrue(Long userId);
    
    // Finds a specific UserEquipment item owned by a user
    Optional<UserEquipment> findByUserIdAndEquipmentId(Long userId, Long equipmentId);
    
    // Finds the currently equipped UserEquipment item of a specific type for a user
    Optional<UserEquipment> findEquippedByUserIdAndType(Long userId, EquipmentType type);
    
    // Checks if a user already owns a specific equipment item
    boolean existsByUserIdAndEquipmentId(Long userId, Long equipmentId);
}