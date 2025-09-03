package ingsoftware.controller.strictly_view;

import ingsoftware.model.Equipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Manager per la gestione della visualizzazione di una riga di equipaggiamento.
 * Questa classe si occupa esclusivamente degli aspetti di UI relativi alla visualizzazione
 * e interazione con una singola riga di equipaggiamento.
 */
public class EquipmentRowManager {

    private final HBox container;
    private final ChoiceBox<Equipment> choiceBox;
    private final Label slotLabel;
    private final ImageView preview;

    public EquipmentRowManager(EquipmentType type, ObservableList<Equipment> items) {
        this.slotLabel = new Label(type.getDisplayName());
        this.choiceBox = createChoiceBox(items);
        this.preview = new ImageView();
        this.container = new HBox(10, slotLabel, choiceBox, preview);

        setupChoiceBox();
        styleComponents();
    }

    private ChoiceBox<Equipment> createChoiceBox(ObservableList<Equipment> items) {
        ChoiceBox<Equipment> cb = new ChoiceBox<>(items);
        // Qui si potrebbe aggiungere un StringConverter personalizzato
        // cb.setConverter(new EquipmentStringConverter());
        return cb;
    }

    private void setupChoiceBox() {
        choiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> {
            //updatePreview(selected);
        });
    }

    private void updatePreview(Equipment equipment) {
        if (equipment != null && !equipment.isNoneOption() && equipment.getIconPath() != null) {
            preview.setImage(new Image(equipment.getIconPath()));
        } else {
            preview.setImage(null);
        }
    }

    private void styleComponents() {
        slotLabel.setMinWidth(80);
        choiceBox.setMinWidth(200);
        preview.setFitWidth(48);
        preview.setFitHeight(48);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5));
    }

    public void setSelectedEquipment(Equipment equipment) {
        choiceBox.setValue(equipment);
    }

    public Node getNode() {
        return container;
    }

    public Equipment getSelectedEquipment() {
        return choiceBox.getValue();
    }
}

