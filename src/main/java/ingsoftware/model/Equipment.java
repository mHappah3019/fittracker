package ingsoftware.model;

import ingsoftware.model.enum_helpers.EquipmentType;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "equipments")
public class Equipment {

    // ========== FIELDS ==========
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private EquipmentType type;
    private double experienceMultiplier = 1.0;
    private boolean available = true; // Indicates if equipment is available in catalog
    private transient boolean noneOption = false; // Added for "None" option

    // ========== CONSTRUCTORS ==========
    
    public Equipment() {
    }

    // ========== STATIC FACTORY METHODS ==========
    
    /**
     * Creates a special "None" option for equipment selection.
     * This option represents no equipment equipped.
     * 
     * @return Equipment instance representing "None" option
     */
    public static Equipment createNoneOption() {
        Equipment none = new Equipment();
        none.setName("Nessuno");
        none.setAvailable(false);
        none.setNoneOption(true);
        return none;
    }

    // ========== GETTERS ==========
    
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Optional<EquipmentType> getType() {
        return Optional.ofNullable(type);
    }

    public double getExperienceMultiplier() {
        return experienceMultiplier;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isNoneOption() {
        return noneOption;
    }

    // ========== SETTERS ==========
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public void setExperienceMultiplier(double experienceMultiplier) {
        this.experienceMultiplier = experienceMultiplier;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    private void setNoneOption(boolean noneOption) {
        this.noneOption = noneOption;
    }

    // ========== UTILITY METHODS ==========
    
    /**
     * Gets the icon path for this equipment.
     */
    public String getIconPath() {
        if (noneOption) {
            return "/icons/none-equipment.png";
        }
        return type != null ? type.getIconPath() : "/icons/default-equipment.png";
    }
    
    /**
     * Gets the type icon for this equipment.
     */
    public String getTypeIcon() {
        if (noneOption) {
            return "⭕";
        }
        return type != null ? type.getIcon() : "❓";
    }
    
    /**
     * Gets formatted display string for experience multiplier.
     */
    public String getMultiplierDisplay() {
        return noneOption ? "1.0x" : String.format("%.1fx", experienceMultiplier);
    }

    // ========== OBJECT METHODS ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipment equipment = (Equipment) o;
        return Objects.equals(id, equipment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        if (noneOption) {
            return "⭕ " + name;
        }
        return getTypeIcon() + " " + name + " (" + getMultiplierDisplay() + ")";
    }
}