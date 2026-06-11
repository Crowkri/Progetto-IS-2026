package Control;

import java.util.Date;
import Entity.SessioneAllenamento;

public class GestoreSessioni {

    private AppSport facade;

    public GestoreSessioni(AppSport facade) {
        this.facade = facade;
    }

    public SessioneAllenamento creaNuovaSessione(Long idAllenatore, Long idAtleta, String titolo, String descrizione, int durataPrevista, Date dataSvolgimento) {
        // Validazione formale dei dati inseriti dalla Boundary (GUI)
        if (titolo == null || titolo.isBlank() || durataPrevista <= 0 || dataSvolgimento == null) {
            throw new IllegalArgumentException("Dati sessione incompleti o non validi.");
        }

        return facade.creaNuovaSessione(idAllenatore, idAtleta, titolo, descrizione, durataPrevista, dataSvolgimento);
    }

    public void aggiungiEsercizioASessione(Long idAllenatore, Long idSessione, String nome, String descrizione, int ripetizioniPreviste, int durataPrevista) {
        if (nome == null || nome.isBlank() || ripetizioniPreviste <= 0) {
            throw new IllegalArgumentException("Dati esercizio non validi.");
        }

        facade.aggiungiEsercizioASessione(idAllenatore, idSessione, nome, descrizione, ripetizioniPreviste, durataPrevista);
    }

    public void registraEsecuzioneEsercizio(Long idAtleta, Long idEsercizio, int ripetizioniEffettive, int tempoImpiegato, String nota) {
        if (ripetizioniEffettive < 0 || tempoImpiegato <= 0) {
            throw new IllegalArgumentException("I dati di esecuzione devono essere positivi.");
        }

        facade.registraEsecuzioneEsercizio(idAtleta, idEsercizio, ripetizioniEffettive, tempoImpiegato, nota);
    }
}