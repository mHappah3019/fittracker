package ingsoftware.dao;

import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;

import java.util.List;
import java.util.Optional;

public interface UserEquipmentDAO extends BaseDAO<UserEquipment, Long> {
    List<UserEquipment> findByUserId(Long userId);
    List<UserEquipment> findByUserIdAndEquippedTrue(Long userId);
    List<UserEquipment> findByUserIdAndEquippedFalse(Long userId);
    Optional<UserEquipment> findByUserIdAndEquipmentId(Long userId, Long equipmentId);
    Optional<UserEquipment> findEquippedByUserIdAndType(Long userId, EquipmentType type);
    List<UserEquipment> findByUserIdAndEquipmentType(Long userId, EquipmentType type);
    boolean existsByUserIdAndEquipmentId(Long userId, Long equipmentId);
}