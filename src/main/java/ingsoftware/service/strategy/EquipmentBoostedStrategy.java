package ingsoftware.service.strategy;

import ingsoftware.model.Equipment;
import ingsoftware.model.EquipmentType;
import ingsoftware.model.User;
import ingsoftware.service.EquipmentService;

import java.util.Map;

public class EquipmentBoostedStrategy implements GamificationStrategy {
    private final GamificationStrategy baseStrategy;
    private final EquipmentService equipmentService;

    public EquipmentBoostedStrategy(GamificationStrategy baseStrategy, EquipmentService equipmentService) {
        this.baseStrategy = baseStrategy;
        this.equipmentService = equipmentService;
    }


    @Override
    public double calculateExperience(double baseExperience, User user) {
        double baseResult = baseStrategy.calculateExperience(baseExperience, user);

        Map<EquipmentType, Equipment> activeEquipments = equipmentService.getFullEquipmentSet(user.getID());
        if (activeEquipments == null || activeEquipments.isEmpty()) {
            return baseResult;
        }

        double totalMultiplier = 1.0;
        for (Equipment equipment : activeEquipments.values()) {
            totalMultiplier *= equipment.getExperienceMultiplier();
        }

        return (baseResult * totalMultiplier);
    }
}
