package ingsoftware.service.strategy;

import ingsoftware.service.EquipmentService;
import ingsoftware.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExperienceStrategyFactory {
    private final EquipmentService equipmentService;
    private final EventService eventService;

    @Autowired
    public ExperienceStrategyFactory(EquipmentService equipmentService, EventService eventService) {
        this.equipmentService = equipmentService;
        this.eventService = eventService;
    }

    public GamificationStrategy createStrategy() {
        GamificationStrategy strategy = createBaseStrategy();
        strategy = applyExponentialBonus(strategy);
        strategy = applyEquipmentBonus(strategy);
        strategy = applyEventBonusIfActive(strategy);
        return strategy;
    }


    private GamificationStrategy createBaseStrategy() {
        return new BaseGamificationStrategy();
    }

    private GamificationStrategy applyExponentialBonus(GamificationStrategy strategy) {
        return new ExponentialExperienceStrategy(strategy);
    }

    private GamificationStrategy applyEquipmentBonus(GamificationStrategy strategy) {
        return new EquipmentBoostedStrategy(strategy, equipmentService);
    }

    private GamificationStrategy applyEventBonusIfActive(GamificationStrategy strategy) {
        if (eventService.isEventBonusActive()) {
            return new EventBonusStrategy(strategy, eventService);
        }
        return strategy;
    }
}