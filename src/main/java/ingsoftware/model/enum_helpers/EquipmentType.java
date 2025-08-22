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
        switch (this) {
            case WEAPON:
                return "Aumenta l'esperienza guadagnata completando le abitudini";
            case ARMOR:
                return "Protegge dalla perdita di streak quando salti un giorno";
            case SHIELD:
                return "Fornisce bonus speciali e moltiplicatori unici";
            case MISC:
                return "Oggetto usa e getta con effetti temporanei potenti";
            default:
                return "Tipo di equipaggiamento sconosciuto";
        }
    }

    /**
     * Verifica se il tipo è compatibile con equipaggiamenti permanenti
     */

    /**
     * Restituisce il colore associato al tipo per la UI
     */
    public String getTypeColor() {
        switch (this) {
            case WEAPON:
                return "#FF6B6B"; // Rosso
            case ARMOR:
                return "#4ECDC4"; // Verde acqua
            case SHIELD:
                return "#45B7D1"; // Blu
            case MISC:
                return "#96CEB4"; // Verde chiaro
            default:
                return "#95A5A6"; // Grigio
        }
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
