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

    public static EquipmentType[] getOrderedValues() {
        return new EquipmentType[] {
                ARMOR,    // Armatura
                SHIELD,   // Scudo
                WEAPON,   // Arma
                MISC      // Miscelaneous
        };
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


    @Override
    public String toString() {
        return displayName + " " + icon;
    }
}
