package Control;

import Entity.SessioneAllenamento;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GestoreDashboard {

    private AppSport facade;

    public GestoreDashboard(AppSport facade) {
        this.facade = facade;
    }

    // Riflette il punto 3.1: Dati aggregati degli atleti
    public Map<String, Integer> getIndicatoriAggregati(Long idAllenatore) {
        if (idAllenatore == null) {
            throw new IllegalArgumentException("ID Allenatore non valido.");
        }

        Map<String, Integer> indicatori = facade.getIndicatoriAggregati(idAllenatore);

        // Flusso alternativo Se non ci sono atleti associati
        if (indicatori == null || indicatori.isEmpty()) {
            throw new IllegalStateException("Nessun atleta associato. Impossibile mostrare la dashboard.");
        }

        return indicatori;
    }

    // Riflette il punto 3.5: Analisi andamento nel tempo (RF12)
    public Map<Date, Integer> getEvoluzioneAtleta(Long idAllenatore, Long idAtleta) {
        if (idAllenatore == null || idAtleta == null) {
            throw new IllegalArgumentException("ID mancanti per l'elaborazione dell'evoluzione.");
        }

        Map<Date, Integer> evoluzione = facade.getEvoluzioneAtleta(idAllenatore, idAtleta);

        // Flusso alternativo Punto 3.5.2: Nessuno storico
        if (evoluzione == null || evoluzione.isEmpty()) {
            throw new IllegalStateException("Dati insufficienti per generare il grafico evolutivo.");
        }

        return evoluzione;
    }

    // Riflette il punto 3.4: Scostamento tra pianificazione ed esecuzione (RF10)
    public Map<String, Double> generaConfrontoPrevistoEffettivo(Long idAllenatore, Long idAtleta) {
        if (idAllenatore == null || idAtleta == null) {
            throw new IllegalArgumentException("ID mancanti per il confronto.");
        }

        Map<String, Double> confronto = facade.generaConfrontoPrevistoEffettivo(idAllenatore, idAtleta);

        // Flusso alternativo Punto 3.4.2: Nessuna prestazione
        if (confronto == null || confronto.isEmpty()) {
            throw new IllegalStateException("Nessun dato registrato per questo atleta. Impossibile effettuare il confronto.");
        }

        return confronto;
    }


}