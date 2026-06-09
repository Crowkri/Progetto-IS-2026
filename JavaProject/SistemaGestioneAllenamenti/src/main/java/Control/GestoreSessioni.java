package Control;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import Entity.SessioneAllenamento;
import Database.GestorePersistenza;
import Entity.Allenatore;
import Entity.Atleta;
import Entity.Esercizio;

public class GestoreSessioni {

    private GestorePersistenza db;

    public GestoreSessioni(GestorePersistenza db) {
        this.db = db;
    }

    public SessioneAllenamento creaNuovaSessione(Allenatore allenatore, Atleta atleta, String titolo, String descrizione, int durataPrevista, Date dataSvolgimento) {
        // Controllo del vincolo di sicurezza V04 (L'allenatore agisce solo sui suoi atleti)
        if (allenatore != null && atleta != null && allenatore.haAtletaAssociato(atleta)) {
            SessioneAllenamento nuovaSessione = new SessioneAllenamento(titolo, descrizione, durataPrevista, dataSvolgimento);

            // Impostiamo la relazione bidirezionale in memoria
            nuovaSessione.setAtleta(atleta);
            atleta.aggiungiSessione(nuovaSessione);

            // Salviamo l'atleta aggiornato. Grazie a 'cascade = CascadeType.ALL' impostato nell'Atleta,
            // JPA salverà automaticamente la nuova sessione nel database.
            // Usiamo aggiorna() perché l'atleta esiste già nel DB.
            db.aggiorna(atleta);
            return nuovaSessione;
        }
        return null;
    }

    public void aggiungiEsercizioASessione(Allenatore allenatore, SessioneAllenamento sessione, String nome, String descrizione, int ripetizioniPreviste, int durataPrevista) {
        // TODO: Andrebbe verificato che l'allenatore sia effettivamente colui che segue l'atleta proprietario della sessione
        if (sessione != null) {
            Esercizio nuovoEsercizio = new Esercizio(nome, descrizione, ripetizioniPreviste, durataPrevista);

            // Il metodo aggiungiEsercizio() gestisce già la coerenza bidirezionale impostando l'esercizio sulla sessione
            sessione.aggiungiEsercizio(nuovoEsercizio);

            // Usiamo aggiorna() perché stiamo modificando una sessione già esistente nel DB
            // inserendo un nuovo record nella tabella esercizi (grazie al cascade)
            db.aggiorna(sessione);
        }
    }

    public void registraEsecuzioneEsercizio(Atleta atleta, Esercizio esercizio, int ripetizioniEffettive, int tempoImpiegato, String nota) {
        if (esercizio != null) {
            // L'atleta compila i dati di performance dell'esercizio
            esercizio.registraEsecuzione(ripetizioniEffettive, tempoImpiegato, nota);

            // Usiamo aggiorna() perché il record dell'esercizio esiste già, lo stiamo solo modificando
            db.aggiorna(esercizio);

            // Qui potresti richiamare un "ServizioMessaggistica" (dal diagramma)
            // per inviare la notifica di completamento all'allenatore (Vincolo 22)
        }
    }

    public boolean eliminaSessione(Allenatore allenatore, SessioneAllenamento sessione) {
        if (sessione != null) {
            // Usiamo l'ID corretto dell'entità e lo passiamo al metodo di eliminazione del DB
            return db.elimina(SessioneAllenamento.class, sessione.getIdSessione());
        }
        return false;
    }

    public List<SessioneAllenamento> getCronologiaSessioni(Atleta atleta, Date daData, Date aData) {
        if (atleta == null) return null;

        // Sfrutta le Stream API per filtrare la collezione di sessioni dell'atleta in base alle date richieste
        return atleta.getSessioniAllenamento().stream()
                .filter(s -> !s.getDataSvolgimento().before(daData) && !s.getDataSvolgimento().after(aData))
                .collect(Collectors.toList());
    }
}