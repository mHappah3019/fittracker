package ingsoftware.repository;

import ingsoftware.model.Equipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByAvailableTrue();
    
    List<Equipment> findByTypeAndAvailableTrue(EquipmentType type);
}
