package ingsoftware.repository;


import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEquipmentRepository extends JpaRepository<UserEquipment, Long> {
    
    /**
     * Trova tutti gli equipaggiamenti di un utente
     */
    List<UserEquipment> findByUserId(Long userId);
    
    /**
     * Trova tutti gli equipaggiamenti equipaggiati di un utente
     */
    List<UserEquipment> findByUserIdAndEquippedTrue(Long userId);
    
    /**
     * Trova tutti gli equipaggiamenti non equipaggiati di un utente
     */
    List<UserEquipment> findByUserIdAndEquippedFalse(Long userId);
    
    /**
     * Trova un equipaggiamento specifico di un utente
     */
    Optional<UserEquipment> findByUserIdAndEquipmentId(Long userId, Long equipmentId);
    
    /**
     * Trova l'equipaggiamento equipaggiato di un tipo specifico per un utente
     */
    @Query("SELECT ue FROM UserEquipment ue JOIN Equipment e ON ue.equipmentId = e.id WHERE ue.userId = :userId AND ue.equipped = true AND e.type = :type")
    Optional<UserEquipment> findEquippedByUserIdAndType(@Param("userId") Long userId, @Param("type") EquipmentType type);
    
    /**
     * Trova tutti gli equipaggiamenti di un tipo specifico per un utente
     */
    @Query("SELECT ue FROM UserEquipment ue JOIN Equipment e ON ue.equipmentId = e.id WHERE ue.userId = :userId AND e.type = :type")
    List<UserEquipment> findByUserIdAndEquipmentType(@Param("userId") Long userId, @Param("type") EquipmentType type);
    
    /**
     * Verifica se un utente possiede un equipaggiamento specifico
     */
    boolean existsByUserIdAndEquipmentId(Long userId, Long equipmentId);
}