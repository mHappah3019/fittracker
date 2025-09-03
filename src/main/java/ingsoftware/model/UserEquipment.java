package ingsoftware.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_equipments")
public class UserEquipment {

    // ========== FIELDS ==========
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "equipment_id", nullable = false)
    private Long equipmentId;
    
    @Column(name = "is_equipped", nullable = false)
    private boolean equipped = false;
    
    @Column(name = "acquired_date", nullable = false)
    private LocalDateTime acquiredDate;
    
    @Column(name = "equipped_date")
    private LocalDateTime equippedDate;

    // ========== CONSTRUCTORS ==========
    
    public UserEquipment() {
        this.acquiredDate = LocalDateTime.now();
    }
    
    public UserEquipment(Long userId, Long equipmentId) {
        this();
        this.userId = userId;
        this.equipmentId = equipmentId;
    }
    
    public UserEquipment(Long userId, Long equipmentId, boolean equipped) {
        this(userId, equipmentId);
        this.equipped = equipped;
        if (equipped) {
            this.equippedDate = LocalDateTime.now();
        }
    }

    // ========== GETTERS ==========
    
    public Long getId() {
        return id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Long getEquipmentId() {
        return equipmentId;
    }
    
    public boolean isEquipped() {
        return equipped;
    }
    
    public LocalDateTime getAcquiredDate() {
        return acquiredDate;
    }
    
    public LocalDateTime getEquippedDate() {
        return equippedDate;
    }

    // ========== SETTERS ==========
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }
    
    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
        if (equipped && this.equippedDate == null) {
            this.equippedDate = LocalDateTime.now();
        } else if (!equipped) {
            this.equippedDate = null;
        }
    }
    
    public void setAcquiredDate(LocalDateTime acquiredDate) {
        this.acquiredDate = acquiredDate;
    }
    
    public void setEquippedDate(LocalDateTime equippedDate) {
        this.equippedDate = equippedDate;
    }

    // ========== BUSINESS METHODS ==========
    
    /**
     * Equips this equipment for the user.
     * Sets equipped status to true and updates equipped date.
     */
    public void equip() {
        this.equipped = true;
        this.equippedDate = LocalDateTime.now();
    }
    
    /**
     * Unequips this equipment from the user.
     * Sets equipped status to false and clears equipped date.
     */
    public void unequip() {
        this.equipped = false;
        this.equippedDate = null;
    }

    // ========== OBJECT METHODS ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEquipment that = (UserEquipment) o;
        return Objects.equals(id, that.id) ||
               (Objects.equals(userId, that.userId) && Objects.equals(equipmentId, that.equipmentId));
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, equipmentId);
    }
    
    @Override
    public String toString() {
        return "UserEquipment{" +
                "id=" + id +
                ", userId=" + userId +
                ", equipmentId=" + equipmentId +
                ", equipped=" + equipped +
                ", acquiredDate=" + acquiredDate +
                ", equippedDate=" + equippedDate +
                '}';
    }
}