package ingsoftware.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // oppure un oggetto AvatarCustomization vero, se vuoi tipizzarlo meglio
import java.util.HashMap;

@Entity
public class User {

    @Id private Long id;
    private String username;
    private int level;
    private double xp;
    private int lifePoints = 100; // valore iniziale
    private LocalDate lastAccessDate;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<HabitCompletion> habitCompletions = new ArrayList<>();

    // Constructor per nuovo utente
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.level = 1;
        this.xp = 0;
        this.lastAccessDate = LocalDate.now();
    }

    // Empty constructor for frameworks/ORM
    public User() {}

    // Getters & setters per tutti i campi...


    public Long getID() {
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

    public void setTotalXp(int i) {
        this.xp = i;
    }
}

