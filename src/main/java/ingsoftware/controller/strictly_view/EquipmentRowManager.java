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

    /**
     * Costruttore che inizializza tutti i componenti UI necessari per una riga di equipaggiamento.
     *
     * @param type Il tipo di equipaggiamento per questa riga
     * @param items La lista di equipaggiamenti disponibili per questo tipo
     */
    public EquipmentRowManager(EquipmentType type, ObservableList<Equipment> items) {
        this.slotLabel = new Label(type.getDisplayName());
        this.choiceBox = createChoiceBox(items);
        this.preview = new ImageView();
        this.container = new HBox(10, slotLabel, choiceBox, preview);

        setupChoiceBox();
        styleComponents();
    }

    /**
     * Crea e configura il ChoiceBox per la selezione dell'equipaggiamento.
     *
     * @param items La lista di equipaggiamenti disponibili
     * @return Il ChoiceBox configurato
     */
    private ChoiceBox<Equipment> createChoiceBox(ObservableList<Equipment> items) {
        ChoiceBox<Equipment> cb = new ChoiceBox<>(items);
        // Qui si potrebbe aggiungere un StringConverter personalizzato
        // cb.setConverter(new EquipmentStringConverter());
        return cb;
    }

    /**
     * Configura il listener per il ChoiceBox per aggiornare l'anteprima quando viene selezionato un nuovo equipaggiamento.
     */
    private void setupChoiceBox() {
        choiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> {
            //updatePreview(selected);
        });
    }

    /**
     * Aggiorna l'anteprima dell'immagine in base all'equipaggiamento selezionato.
     *
     * @param equipment L'equipaggiamento selezionato
     */
    private void updatePreview(Equipment equipment) {
        if (equipment != null && !equipment.isNoneOption() && equipment.getIconPath() != null) {
            preview.setImage(new Image(equipment.getIconPath()));
        } else {
            preview.setImage(null);
        }
    }

    /**
     * Applica stili e dimensioni ai componenti UI.
     */
    private void styleComponents() {
        slotLabel.setMinWidth(80);
        choiceBox.setMinWidth(200);
        preview.setFitWidth(48);
        preview.setFitHeight(48);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5));
    }

    /**
     * Imposta l'equipaggiamento attualmente selezionato.
     *
     * @param equipment L'equipaggiamento da selezionare
     */
    public void setSelectedEquipment(Equipment equipment) {
        choiceBox.setValue(equipment);
    }

    /**
     * Restituisce il nodo principale che rappresenta questa riga di equipaggiamento.
     *
     * @return Il nodo HBox contenente tutti i componenti della riga
     */
    public Node getNode() {
        return container;
    }

    /**
     * Restituisce l'equipaggiamento attualmente selezionato.
     *
     * @return L'equipaggiamento selezionato
     */
    public Equipment getSelectedEquipment() {
        return choiceBox.getValue();
    }
}

