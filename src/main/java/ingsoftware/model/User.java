package ingsoftware.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private int level;
    private double xp;
    private int lifePoints = 100; // valore iniziale
    private LocalDate lastAccessDate;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<HabitCompletion> habitCompletions = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserEquipment> userEquipments = new ArrayList<>();

    // Constructor per nuovo utente
    public User() {
        this.username = "defaultUsername";
        this.level = 1;
        this.xp = 0;
        this.lastAccessDate = LocalDate.now();
    }

    // Getters & setters per tutti i campi...


    public Long getId() {
        return id;
    }

    public double getTotalXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int newLevel) {
        this.level = newLevel;
    }

    
    public void addLifePoints(int delta) {
        this.lifePoints += delta;
        // Evita valori negativi se necessario
        if (this.lifePoints < 0) {
            this.lifePoints = 0;
        }
    }
    
    public int getLifePoints() {
        return lifePoints;
    }
    
    public LocalDate getLastAccessDate() {
        return lastAccessDate;
    }
    
    public void setLastAccessDate(LocalDate lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public void addTotalXp(double gainedXP) {
        this.xp += gainedXP;
        // Evita valori negativi se necessario
        if (this.xp < 0) {
            this.xp = 0;
        }
    }
    
    public List<HabitCompletion> getHabitCompletions() {
        return habitCompletions;
    }
    
    public void setHabitCompletions(List<HabitCompletion> habitCompletions) {
        this.habitCompletions = habitCompletions;
    }

    public void setId(Long userId) {
        this.id = userId;
    }

    public void setTotalXp(double i) {
        this.xp = i;
    }
    
    public List<UserEquipment> getUserEquipments() {
        return userEquipments;
    }
    
    public void setUserEquipments(List<UserEquipment> userEquipments) {
        this.userEquipments = userEquipments;
    }
    
    public void addUserEquipment(UserEquipment userEquipment) {
        this.userEquipments.add(userEquipment);
        userEquipment.setUser(this);
    }
    
    public void removeUserEquipment(UserEquipment userEquipment) {
        this.userEquipments.remove(userEquipment);
        userEquipment.setUser(null);
    }
}

