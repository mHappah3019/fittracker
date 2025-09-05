package ingsoftware.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

    // ========== FIELDS ==========
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    private int level;
    private double xp;
    private int lifePoints = 100; // valore iniziale
    private LocalDate lastAccessDate;

    // ========== CONSTRUCTORS ==========
    
    public User() {
        this.username = "defaultUsername";
        this.level = 1;
        this.xp = 0;
        this.lastAccessDate = LocalDate.now();
    }

    // ========== GETTERS ==========
    
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getLevel() {
        return level;
    }

    public double getTotalXp() {
        return xp;
    }

    public int getLifePoints() {
        return lifePoints;
    }

    public LocalDate getLastAccessDate() {
        return lastAccessDate;
    }

    // ========== SETTERS ==========
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setTotalXp(double xp) {
        this.xp = xp;
    }

    public void setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
    }

    public void setLastAccessDate(LocalDate lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    // ========== BUSINESS METHODS ==========
    
    /**
     * Aggiunge XP al totale dell'utente.
     * Previene valori negativi impostando XP a 0 se il risultato è negativo.
     */
    public void addTotalXp(double gainedXP) {
        this.xp += gainedXP;
        if (this.xp < 0) {
            this.xp = 0;
        }
    }

    /**
     * Aggiunge punti vita all'utente.
     * Previene valori negativi impostando i punti vita a 0 se il risultato è negativo.
     */
    public void addLifePoints(int delta) {
        this.lifePoints += delta;
        if (this.lifePoints < 0) {
            this.lifePoints = 0;
        } else if (this.lifePoints > 100) {
            this.lifePoints = 100;
        }
    }
}

