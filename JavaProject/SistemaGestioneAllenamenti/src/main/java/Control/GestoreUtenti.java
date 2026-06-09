package Control;

import java.util.List;
import Entity.Allenatore;
import Entity.Utente;
import Database.GestorePersistenza;
import Entity.Atleta;
import Entity.ProfiloAtleta;

public class GestoreUtenti {

    // Riferimento al livello di persistenza (Database)
    private GestorePersistenza db;

    public GestoreUtenti(GestorePersistenza db) {
        this.db = db;
    }
    public Atleta registraAtleta(String nome, String cognome, String email, String password, String disciplina) {
        Atleta nuovoAtleta = new Atleta(nome, cognome, email, password, disciplina);
        db.salva(nuovoAtleta);
        return nuovoAtleta;
    }

    public Allenatore registraAllenatore(String nome, String cognome, String email, String password, String disciplina, String codiceAssociazione) {
        Allenatore nuovoAllenatore = new Allenatore(codiceAssociazione, nome, cognome, email, password, disciplina);
        db.salva(nuovoAllenatore);
        return nuovoAllenatore;
    }

    // CORREZIONE JPA: Non potendo interrogare la classe astratta, interroghiamo le tabelle figlie
    public Utente autenticaUtente(String email, String password) {

        // 1. Cerchiamo prima tra gli Atleti
        List<Atleta> atleti = db.cercaPerCampo(Atleta.class, "email", email);
        if (atleti != null && !atleti.isEmpty()) {
            Atleta atleta = atleti.get(0);
            if (atleta.getPassword().equals(password)) {
                return atleta;
            }
        }

        // 2. Se non è un atleta, cerchiamo tra gli Allenatori
        List<Allenatore> allenatori = db.cercaPerCampo(Allenatore.class, "email", email);
        if (allenatori != null && !allenatori.isEmpty()) {
            Allenatore allenatore = allenatori.get(0);
            if (allenatore.getPassword().equals(password)) {
                return allenatore;
            }
        }

        return null; // Credenziali errate o utente non trovato
    }

    // ========================================================================
    // METODI DI ASSOCIAZIONE
    // ========================================================================

    public boolean associaConCodice(Atleta atleta, String codiceInserito) {
        List<Allenatore> risultati = db.cercaPerCampo(Allenatore.class, "codicePerAssociare", codiceInserito);

        if (risultati != null && !risultati.isEmpty()) {
            Allenatore allenatore = risultati.get(0);

            if (allenatore.aggiungiAtleta(atleta)) {
                db.aggiorna(atleta); // L'atleta è il lato proprietario (Foreign Key)
                db.aggiorna(allenatore);
                return true;
            }
        }
        return false;
    }

    public boolean associazioneDiretta(Allenatore allenatore, Long idAtleta) {
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

        if (atleta != null) {
            if (allenatore.aggiungiAtleta(atleta)) {
                db.aggiorna(atleta);
                db.aggiorna(allenatore);
                return true;
            }
        }
        return false;
    }

    // ========================================================================
    // GESTIONE PROFILO ATLETA
    // ========================================================================

    public ProfiloAtleta getProfiloAtleta(Utente richiedente, Atleta atleta) {
        if (atleta == null) return null;

        if (richiedente.equals(atleta)) {
            return atleta.getProfilo();
        }

        if (richiedente instanceof Allenatore) {
            Allenatore allenatore = (Allenatore) richiedente;
            if (allenatore.haAtletaAssociato(atleta)) {
                return atleta.getProfilo();
            }
        }

        throw new SecurityException("Accesso negato: Non sei autorizzato a visualizzare questo profilo.");
    }

    public void modificaProfiloAtleta(Allenatore allenatore, Atleta atleta, String disciplina, String livelloEsperienza, String obiettiviSportivi) {

        if (allenatore != null && atleta != null && allenatore.haAtletaAssociato(atleta)) {
            // Crea l'oggetto @Embeddable e lo passa all'atleta
            ProfiloAtleta nuovoProfilo = new ProfiloAtleta(disciplina, livelloEsperienza, obiettiviSportivi);
            atleta.impostaProfilo(nuovoProfilo);

            db.aggiorna(atleta);
        } else {
            throw new IllegalArgumentException("Errore di autorizzazione: L'atleta non è associato a questo allenatore.");
        }
    }
}