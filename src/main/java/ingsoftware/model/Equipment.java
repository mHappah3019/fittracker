package ingsoftware.model;
import ingsoftware.model.enum_helpers.EquipmentType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;
import java.util.Optional;

@Entity
public class Equipment {
    public enum EquipmentState {
        NONE {
            @Override
            public String getIconPath(EquipmentType type) {
                return "/icons/none-equipment.png";
            }
            @Override
            public String getTypeIcon(EquipmentType type) {
                return "⭕";
            }
            @Override
            public String getStatusDisplay() {
                return "➖ Non equipaggiato";
            }
            @Override
            public boolean isActive() {
                return false;
            }
        },
        ACTIVE {
            @Override
            public String getIconPath(EquipmentType type) {
                return type != null ? type.getIconPath() : "/icons/default-equipment.png";
            }
            @Override
            public String getTypeIcon(EquipmentType type) {
                return type != null ? type.getIcon() : "❓";
            }
            @Override
            public String getStatusDisplay() {
                return "🟢 Attivo";
            }
            @Override
            public boolean isActive() {
                return true;
            }
        },
        INACTIVE {
            @Override
            public String getIconPath(EquipmentType type) {
                return type != null ? type.getIconPath() : "/icons/default-equipment.png";
            }
            @Override
            public String getTypeIcon(EquipmentType type) {
                return type != null ? type.getIcon() : "❓";
            }
            @Override
            public String getStatusDisplay() {
                return "🔴 Disattivato";
            }
            @Override
            public boolean isActive() {
                return false;
            }
        };
        public abstract String getIconPath(EquipmentType type);
        public abstract String getTypeIcon(EquipmentType type);
        public abstract String getStatusDisplay();
        public abstract boolean isActive();
    }

    @Id private Long id;
    private String name;
    private String description;
    private EquipmentType type;
    private double experienceMultiplier = 1.0;
    private EquipmentState state = EquipmentState.NONE;
    private transient boolean noneOption = false; // Aggiunto per l'opzione "Nessuno"

    // Metodo statico per creare l'opzione "Nessuno"
    public static Equipment createNoneOption() {
        Equipment none = new Equipment();
        none.setName("Nessuno");
        none.setState(EquipmentState.NONE);
        none.noneOption = true;
        return none;
    }
    // Metodo per verificare se è l'opzione "Nessuno"
    public boolean isNoneOption() {
        return noneOption;
    }
    // Metodi di stato migliorati
    public boolean isNoneEquipment() {
        return state == EquipmentState.NONE;
    }
    public boolean isActive() {
        return state.isActive();
    }
    // Metodi di utilità migliorati
    public String getIconPath() {
        return state.getIconPath(type);
    }
    public String getTypeIcon() {
        return state.getTypeIcon(type);
    }
    public String getStatusDisplay() {
        return state.getStatusDisplay();
    }
    public String getMultiplierDisplay() {
        return isNoneEquipment() ? "1.0x" : String.format("%.1fx", experienceMultiplier);
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

    public EquipmentState getState() {
        return state;
    }
    public void setState(EquipmentState state) {
        this.state = state != null ? state : EquipmentState.NONE;
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