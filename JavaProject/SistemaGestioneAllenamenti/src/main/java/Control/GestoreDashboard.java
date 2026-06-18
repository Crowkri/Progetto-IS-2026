package Control;

import Entity.AppSport;

import java.util.Date;
import java.util.Map;

public class GestoreDashboard {

    private AppSport facade;

    public GestoreDashboard(AppSport facade) {
        this.facade = facade;
    }

    public Map<String, Integer> getIndicatoriAggregati(Long idAllenatore) {
        if (idAllenatore == null) {
            throw new IllegalArgumentException("ID Allenatore non valido.");
        }

        Map<String, Integer> indicatori = facade.getIndicatoriAggregati(idAllenatore);

        // Flusso alternativo: No atleti associati
        if (indicatori == null || indicatori.isEmpty()) {
            throw new IllegalStateException("Nessun atleta associato. Impossibile mostrare la dashboard.");
        }

        return indicatori;
    }

    public Map<Date, Integer> getEvoluzioneAtleta(Long idAllenatore, Long idAtleta) {
        if (idAllenatore == null || idAtleta == null) {
            throw new IllegalArgumentException("ID mancanti per l'elaborazione dell'evoluzione.");
        }

        Map<Date, Integer> evoluzione = facade.getEvoluzioneAtleta(idAllenatore, idAtleta);

        // Flusso alternativo: Nessuno storico
        if (evoluzione == null || evoluzione.isEmpty()) {
            throw new IllegalStateException("Dati insufficienti per generare il grafico evolutivo.");
        }

        return evoluzione;
    }

    public Map<String, Double> generaConfrontoPrevistoEffettivo(Long idAllenatore, Long idAtleta) {
        if (idAllenatore == null || idAtleta == null) {
            throw new IllegalArgumentException("ID mancanti per il confronto.");
        }

        Map<String, Double> confronto = facade.generaConfrontoPrevistoEffettivo(idAllenatore, idAtleta);

        // Flusso alternativo: Nessuna prestazione
        if (confronto == null || confronto.isEmpty()) {
            throw new IllegalStateException("Nessun dato registrato per questo atleta. Impossibile effettuare il confronto.");
        }

        return confronto;
    }


}