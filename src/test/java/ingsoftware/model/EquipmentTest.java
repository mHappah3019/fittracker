package ingsoftware.model;

import ingsoftware.model.enum_helpers.EquipmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EquipmentTest {

    private Equipment equipment;

    @BeforeEach
    void setUp() {
        equipment = new Equipment();
    }

    @Test
    void constructor_shouldCreateEquipmentWithDefaults() {
        // Act
        Equipment newEquipment = new Equipment();

        // Assert
        assertThat(newEquipment.getExperienceMultiplier()).isEqualTo(1.0);
        assertThat(newEquipment.isAvailable()).isTrue();
        assertThat(newEquipment.isNoneOption()).isFalse();
    }

    @Test
    void createNoneOption_shouldCreateNoneEquipment() {
        // Act
        Equipment noneEquipment = Equipment.createNoneOption();

        // Assert
        assertThat(noneEquipment.getName()).isEqualTo("Nessuno");
        assertThat(noneEquipment.isAvailable()).isFalse();
        assertThat(noneEquipment.isNoneOption()).isTrue();
    }

    @Test
    void getMultiplierDisplay_shouldReturn1_0x_whenNoneOption() {
        // Arrange
        Equipment noneEquipment = Equipment.createNoneOption();

        // Act & Assert
        assertThat(noneEquipment.getMultiplierDisplay()).isEqualTo("1.0x");
    }

    @Test
    void getMultiplierDisplay_shouldReturnFormattedMultiplier_whenNotNoneOption() {
        // Arrange
        equipment.setExperienceMultiplier(1.5);

        // Act & Assert
        assertThat(equipment.getMultiplierDisplay()).isEqualTo("1.5x");
    }

    @Test
    void getMultiplierDisplay_shouldHandleDecimalValues() {
        // Arrange
        equipment.setExperienceMultiplier(2.75);

        // Act & Assert
        assertThat(equipment.getMultiplierDisplay()).isEqualTo("2.8x"); // Rounded to 1 decimal place
    }

    @Test
    void isAvailable_shouldReturnTrue_byDefault() {
        // Act & Assert
        assertThat(equipment.isAvailable()).isTrue();
    }

    @Test
    void setAvailable_shouldSetAvailabilityCorrectly() {
        // Act
        equipment.setAvailable(false);

        // Assert
        assertThat(equipment.isAvailable()).isFalse();
    }

    @Test
    void getType_shouldReturnOptionalEmpty_whenTypeIsNull() {
        // Act
        Optional<EquipmentType> result = equipment.getType();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getType_shouldReturnOptionalWithType_whenTypeIsSet() {
        // Arrange
        equipment.setType(EquipmentType.WEAPON);

        // Act
        Optional<EquipmentType> result = equipment.getType();

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(EquipmentType.WEAPON);
    }



    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        // Arrange
        Long id = 123L;
        String name = "Test Equipment";
        String description = "Test Description";
        double multiplier = 1.5;

        // Act
        equipment.setId(id);
        equipment.setName(name);
        equipment.setDescription(description);
        equipment.setExperienceMultiplier(multiplier);

        // Assert
        assertThat(equipment.getId()).isEqualTo(id);
        assertThat(equipment.getName()).isEqualTo(name);
        assertThat(equipment.getDescription()).isEqualTo(description);
        assertThat(equipment.getExperienceMultiplier()).isEqualTo(multiplier);
    }

    @Test
    void equals_shouldReturnTrue_whenSameId() {
        // Arrange
        Equipment equipment1 = new Equipment();
        equipment1.setId(1L);

        Equipment equipment2 = new Equipment();
        equipment2.setId(1L);

        // Act & Assert
        assertThat(equipment1).isEqualTo(equipment2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        // Arrange
        Equipment equipment1 = new Equipment();
        equipment1.setId(1L);

        Equipment equipment2 = new Equipment();
        equipment2.setId(2L);

        // Act & Assert
        assertThat(equipment1).isNotEqualTo(equipment2);
    }

    @Test
    void equals_shouldReturnFalse_whenOneIdIsNull() {
        // Arrange
        Equipment equipment1 = new Equipment();
        equipment1.setId(1L);

        Equipment equipment2 = new Equipment();
        // id is null

        // Act & Assert
        assertThat(equipment1).isNotEqualTo(equipment2);
    }

    @Test
    void equals_shouldReturnTrue_whenBothIdsAreNull() {
        // Arrange
        Equipment equipment1 = new Equipment();
        Equipment equipment2 = new Equipment();
        // both ids are null

        // Act & Assert
        assertThat(equipment1).isEqualTo(equipment2);
    }

    @Test
    void equals_shouldReturnTrue_whenSameObject() {
        // Act & Assert
        assertThat(equipment).isEqualTo(equipment);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToNull() {
        // Act & Assert
        assertThat(equipment).isNotEqualTo(null);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToDifferentClass() {
        // Act & Assert
        assertThat(equipment).isNotEqualTo("not an equipment");
    }

    @Test
    void hashCode_shouldBeSame_whenSameId() {
        // Arrange
        Equipment equipment1 = new Equipment();
        equipment1.setId(1L);

        Equipment equipment2 = new Equipment();
        equipment2.setId(1L);

        // Act & Assert
        assertThat(equipment1.hashCode()).isEqualTo(equipment2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentId() {
        // Arrange
        Equipment equipment1 = new Equipment();
        equipment1.setId(1L);

        Equipment equipment2 = new Equipment();
        equipment2.setId(2L);

        // Act & Assert
        assertThat(equipment1.hashCode()).isNotEqualTo(equipment2.hashCode());
    }
}