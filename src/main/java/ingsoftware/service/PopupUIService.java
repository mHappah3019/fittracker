package ingsoftware.service;

import ingsoftware.model.DTO.CompletionResultDTO;


public interface PopupUIService {
    /**
     * Mostra un popup di completamento abitudine.
     */
    void showPopup(CompletionResultDTO result);

    /**
     * Mostra un popup per l'aggiornamento dei punti vita.
     * @param lifePointsDelta La variazione dei punti vita (positiva o negativa)
     * @param reason La ragione dell'aggiornamento (es. "Completamento abitudini", "Inattività")
     */
}
