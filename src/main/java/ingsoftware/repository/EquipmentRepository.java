package ingsoftware.repository;

import ingsoftware.model.Equipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByAvailableTrue();
    
    List<Equipment> findByType(EquipmentType type);
    
    List<Equipment> findByTypeAndAvailableTrue(EquipmentType type);
}
