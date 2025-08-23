package ingsoftware.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_equipments")
public class UserEquipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;
    
    @Column(name = "is_equipped", nullable = false)
    private boolean equipped = false;
    
    @Column(name = "acquired_date", nullable = false)
    private LocalDateTime acquiredDate;
    
    @Column(name = "equipped_date")
    private LocalDateTime equippedDate;
    
    // Costruttori
    public UserEquipment() {
        this.acquiredDate = LocalDateTime.now();
    }
    
    public UserEquipment(User user, Equipment equipment) {
        this();
        this.user = user;
        this.equipment = equipment;
    }
    
    public UserEquipment(User user, Equipment equipment, boolean equipped) {
        this(user, equipment);
        this.equipped = equipped;
        if (equipped) {
            this.equippedDate = LocalDateTime.now();
        }
    }
    
    // Metodi di utilità
    public void equip() {
        this.equipped = true;
        this.equippedDate = LocalDateTime.now();
    }
    
    public void unequip() {
        this.equipped = false;
        this.equippedDate = null;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Equipment getEquipment() {
        return equipment;
    }
    
    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }
    
    public boolean isEquipped() {
        return equipped;
    }
    
    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
        if (equipped && this.equippedDate == null) {
            this.equippedDate = LocalDateTime.now();
        } else if (!equipped) {
            this.equippedDate = null;
        }
    }
    
    public LocalDateTime getAcquiredDate() {
        return acquiredDate;
    }
    
    public void setAcquiredDate(LocalDateTime acquiredDate) {
        this.acquiredDate = acquiredDate;
    }
    
    public LocalDateTime getEquippedDate() {
        return equippedDate;
    }
    
    public void setEquippedDate(LocalDateTime equippedDate) {
        this.equippedDate = equippedDate;
    }
    
    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEquipment that = (UserEquipment) o;
        return Objects.equals(id, that.id) ||
               (Objects.equals(user, that.user) && Objects.equals(equipment, that.equipment));
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(user, equipment);
    }
    
    @Override
    public String toString() {
        return "UserEquipment{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : null) +
                ", equipment=" + (equipment != null ? equipment.getName() : null) +
                ", equipped=" + equipped +
                ", acquiredDate=" + acquiredDate +
                ", equippedDate=" + equippedDate +
                '}';
    }
}