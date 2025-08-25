package ingsoftware.model.enum_helpers;

public enum EquipmentType {
    WEAPON("Arma", "⚔️", "/icons/weapon.png"),
    ARMOR("Armatura", "🛡️", "/icons/armor.png"),
    SHIELD("Scudo", "💍", "/icons/shield.png"),
    MISC("Miscelaneous", "🧪", "/icons/miscelaneous.png");

    private final String displayName;
    private final String icon;
    private final String iconPath;

    // Costruttore
    EquipmentType(String displayName, String icon, String iconPath) {
        this.displayName = displayName;
        this.icon = icon;
        this.iconPath = iconPath;
    }

    // **METODI PUBBLICI**

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    public String getIconPath() {
        return iconPath;
    }


    // **METODI DI UTILITÀ**

    /**
     * Restituisce una descrizione dettagliata del tipo di equipaggiamento
     */
    public String getDescription() {
        return switch (this) {
            case WEAPON -> "Aumenta l'esperienza guadagnata completando le abitudini";
            case ARMOR -> "Protegge dalla perdita di streak quando salti un giorno";
            case SHIELD -> "Fornisce bonus speciali e moltiplicatori unici";
            case MISC -> "Oggetto usa e getta con effetti temporanei potenti";
        };
    }

    /**
     * Restituisce il colore associato al tipo per la UI
     */
    public String getTypeColor() {
        return switch (this) {
            case WEAPON -> "#FF6B6B"; // Rosso
            case ARMOR -> "#4ECDC4"; // Verde acqua
            case SHIELD -> "#45B7D1"; // Blu
            case MISC -> "#96CEB4"; // Verde chiaro
            default -> "#95A5A6"; // Grigio
        };
    }



    /**
     * Trova un tipo per nome (case-insensitive)
     */
    public static EquipmentType fromDisplayName(String displayName) {
        for (EquipmentType type : values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Trova un tipo per icona
     */
    public static EquipmentType fromIcon(String icon) {
        for (EquipmentType type : values()) {
            if (type.getIcon().equals(icon)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName + " " + icon;
    }
}
