package ingsoftware.model;
import ingsoftware.model.enum_helpers.EquipmentType;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "equipments")
public class Equipment {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private EquipmentType type;
    private double experienceMultiplier = 1.0;
    private boolean available = true; // Indica se l'equipaggiamento è disponibile nel catalogo
    private transient boolean noneOption = false; // Aggiunto per l'opzione "Nessuno"

    // Metodo statico per creare l'opzione "Nessuno"
    public static Equipment createNoneOption() {
        Equipment none = new Equipment();
        none.setName("Nessuno");
        none.available = false;
        none.noneOption = true;
        return none;
    }
    
    // Metodo per verificare se è l'opzione "Nessuno"
    public boolean isNoneOption() {
        return noneOption;
    }
    
    // Metodi di utilità semplificati
    public String getIconPath() {
        if (noneOption) {
            return "/icons/none-equipment.png";
        }
        return type != null ? type.getIconPath() : "/icons/default-equipment.png";
    }
    
    public String getTypeIcon() {
        if (noneOption) {
            return "⭕";
        }
        return type != null ? type.getIcon() : "❓";
    }
    
    public String getMultiplierDisplay() {
        return noneOption ? "1.0x" : String.format("%.1fx", experienceMultiplier);
    }
    // Getter e setter completi
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setType(EquipmentType type) {
        this.type = type;
    }
    public Optional<EquipmentType> getType() {
        return Optional.ofNullable(type);
    }
    public double getExperienceMultiplier() {
        return experienceMultiplier;
    }
    public void setExperienceMultiplier(double experienceMultiplier) {
        this.experienceMultiplier = experienceMultiplier;
    }

    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    // Metodi equals e hashCode
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
}