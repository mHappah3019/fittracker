package ingsoftware.dao;

import ingsoftware.model.Equipment;
import ingsoftware.model.enum_helpers.EquipmentType;

import java.util.List;

public interface EquipmentDAO extends BaseDAO<Equipment, Long> {
    List<Equipment> findByAvailableTrue();
    List<Equipment> findByTypeAndAvailableTrue(EquipmentType type);
}
