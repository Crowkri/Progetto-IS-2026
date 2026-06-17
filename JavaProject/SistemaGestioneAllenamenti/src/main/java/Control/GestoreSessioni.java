package Control;

import java.util.Date;

import Entity.AppSport;
import Entity.SessioneAllenamento;

import java.util.List;

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
    public Entity.SessioneAllenamento getSessioneById(Long idSessione) {
        if (idSessione == null) {
            throw new IllegalArgumentException("ID Sessione non valido (null).");
        }

        return facade.getSessioneById(idSessione);
    }

    public void registraEsecuzioneEsercizio(Long idAtleta, Long idEsercizio, int ripetizioniEffettive, int tempoImpiegato, String nota) {
        if (ripetizioniEffettive < 0 || tempoImpiegato <= 0) {
            throw new IllegalArgumentException("I dati di esecuzione devono essere positivi.");
        }

        facade.registraEsecuzioneEsercizio(idAtleta, idEsercizio, ripetizioniEffettive, tempoImpiegato, nota);
    }
    public List<SessioneAllenamento> getSessioniAtleta(Long idAtleta) {
        if (idAtleta == null) {
            throw new IllegalArgumentException("ID Atleta non valido.");
        }
        // Delega la chiamata al Facade
        return facade.getSessioniAtleta(idAtleta);
    }
    public List<SessioneAllenamento> getSessioniAtletaFiltrate(Long idAtleta, String stato, String titolo, Date data) {
        if (idAtleta == null) {
            throw new IllegalArgumentException("Errore: ID Atleta non fornito.");
        }
        // Delega tutto al Facade!
        return facade.filtraSessioniAtleta(idAtleta, stato, titolo, data);
    }
    public List<Entity.Esercizio> getEserciziSessione(Long idSessione) {
        if (idSessione == null) {
            throw new IllegalArgumentException("ID Sessione non valido.");
        }
        return facade.getEserciziSessione(idSessione);
    }

}