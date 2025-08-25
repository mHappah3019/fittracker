package ingsoftware.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    private int level;
    private double xp;
    private int lifePoints = 100; // valore iniziale
    private LocalDate lastAccessDate;
    


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

    public void setId(Long userId) {
        this.id = userId;
    }

    public void setTotalXp(double i) {
        this.xp = i;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
    }
    

}

