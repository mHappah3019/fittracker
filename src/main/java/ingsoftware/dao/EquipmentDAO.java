package ingsoftware.dao;

import ingsoftware.model.Equipment;
import ingsoftware.model.enum_helpers.EquipmentType;

import java.util.List;

/**
 * DAO interface for Equipment entity operations.
 * Handles equipment queries with availability and type filtering.
 */
public interface EquipmentDAO extends BaseDAO<Equipment, Long> {
    // Retrieves all equipment items that are currently available for users
    List<Equipment> findByAvailableTrue();
    
    // Finds available equipment filtered by specific type (weapon, armor, etc.)
    List<Equipment> findByTypeAndAvailableTrue(EquipmentType type);
}
