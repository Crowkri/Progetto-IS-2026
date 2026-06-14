package Control;

import java.util.*;

import Database.GestorePersistenza;
import Entity.Allenatore;
import Entity.Atleta;
import Entity.Esercizio;
import Entity.ProfiloAtleta;
import Entity.SessioneAllenamento;
import Entity.Utente;
import Entity.StatoSessione;

import java.util.stream.Collectors;

public class AppSport {

    private GestorePersistenza db;

    public AppSport() {
        // Il Facade inizializza e gestisce il livello di persistenza
        this.db = new GestorePersistenza();
    }


    // METODI PER GESTORE UTENTI

    public Utente autenticaUtente(String email, String password) {
        List<Atleta> atleti = db.cercaPerCampo(Atleta.class, "email", email);
        if (atleti != null && !atleti.isEmpty() && atleti.get(0).getPassword().equals(password)) {
            return atleti.get(0);
        }

        List<Allenatore> allenatori = db.cercaPerCampo(Allenatore.class, "email", email);
        if (allenatori != null && !allenatori.isEmpty() && allenatori.get(0).getPassword().equals(password)) {
            return allenatori.get(0);
        }
        return null;
    }

    public Atleta registraAtleta(String nome, String cognome, String email, String password, String disciplina, String codiceAssociazione) {
        Atleta nuovoAtleta = new Atleta(nome, cognome, email, password, disciplina);
        db.salva(nuovoAtleta);

        // Logica di business: associazione immediata se codice presente
        if (codiceAssociazione != null && !codiceAssociazione.isEmpty()) {
            List<Allenatore> allenatori = db.cercaPerCampo(Allenatore.class, "codicePerAssociare", codiceAssociazione);
            if (allenatori != null && !allenatori.isEmpty()) {
                Allenatore allenatore = allenatori.get(0);
                if (allenatore.aggiungiAtleta(nuovoAtleta)) {
                    db.aggiorna(nuovoAtleta);
                    db.aggiorna(allenatore);
                }
            }
        }
        return nuovoAtleta;
    }

    public Allenatore registraAllenatore(String nome, String cognome, String email, String password, String disciplina, String codiceAssociazione) {
        // Creazione dell'entità e salvataggio diretto (logica di business/persistenza)
        Allenatore nuovoAllenatore = new Allenatore(codiceAssociazione, nome, cognome, email, password, disciplina);
        db.salva(nuovoAllenatore);

        return nuovoAllenatore;
    }

    public void modificaProfiloAtleta(Long idAllenatore, Long idAtleta, String disciplina, String livelloEsperienza, String obiettiviSportivi) {
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

        // Controllo logica di dominio
        if (allenatore != null && atleta != null && allenatore.haAtletaAssociato(atleta)) {
            ProfiloAtleta nuovoProfilo = new ProfiloAtleta(disciplina, livelloEsperienza, obiettiviSportivi);
            atleta.impostaProfilo(nuovoProfilo);
            db.aggiorna(atleta);
        } else {
            throw new IllegalArgumentException("Autorizzazione negata o utenti inesistenti.");
        }
    }
    public Allenatore associaConCodice(Long idAtleta, String codice) {
        if (codice == null || codice.trim().isEmpty()) {
            return null; // Codice non valido
        }

        // 1. Recuperiamo l'atleta dal DB
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);
        if (atleta == null) {
            throw new IllegalArgumentException("Atleta non trovato nel sistema.");
        }

        // 2. Cerchiamo l'allenatore tramite il codice associativo
        List<Allenatore> allenatori = db.cercaPerCampo(Allenatore.class, "codicePerAssociare", codice.trim());

        if (allenatori != null && !allenatori.isEmpty()) {
            Allenatore allenatore = allenatori.get(0);

            // 3. Sfruttiamo il tuo metodo dell'entità che gestisce già il legame bidirezionale
            boolean associato = allenatore.aggiungiAtleta(atleta);

            if (associato) {
                // 4. Salviamo i cambiamenti nel Database
                db.aggiorna(allenatore);
                db.aggiorna(atleta);
            }

            return allenatore;
        }

        return null; // Nessun allenatore trovato con questo codice
    }
    public boolean associazioneDiretta(Long idAllenatore, Long idAtleta) {
        if (idAllenatore == null || idAtleta == null) {
            return false;
        }

        // 1. Recupero le due entità dal Database
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

        if (allenatore == null) {
            throw new IllegalArgumentException("Errore: Allenatore non trovato nel sistema.");
        }
        if (atleta == null) {
            throw new IllegalArgumentException("Errore: Nessun atleta trovato con questo ID.");
        }

        // 2. Sfruttiamo il metodo bidirezionale dell'entità Allenatore
        boolean associato = allenatore.aggiungiAtleta(atleta);

        // 3. Se l'aggiunta ha successo (ovvero non erano già collegati) salviamo sul DB
        if (associato) {
            db.aggiorna(allenatore);
            db.aggiorna(atleta);
            return true;
        }

        // Ritorna false se l'atleta era già presente nella lista dell'allenatore
        return false;
    }


    public void dissociaAllenatore(Long idAtleta) {
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

        if (atleta != null) {
            // Se l'atleta ha effettivamente un allenatore associato
            Allenatore allenatoreAttuale = atleta.getAllenatoreAssociato();

            if (allenatoreAttuale != null) {
                allenatoreAttuale.rimuoviAtleta(atleta);

                // Aggiorniamo le modifiche sul DB
                db.aggiorna(allenatoreAttuale);
                db.aggiorna(atleta);
            }
        }
    }


    // METODI PER GESTORE SESSIONI

    /**
     * MODIFICATO: Garantisce che l'oggetto ritornato contenga l'ID generato automaticamente
     * dal database, evitando il successivo errore 'id to load is required for loading'.
     */
    public SessioneAllenamento creaNuovaSessione(Long idAllenatore, Long idAtleta, String titolo, String descrizione, int durataPrevista, Date dataSvolgimento) {
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

        if (allenatore != null && atleta != null && allenatore.haAtletaAssociato(atleta)) {

            // FIX DATA: Convertiamo java.util.Date in java.sql.Date
            java.sql.Date dataSQL = new java.sql.Date(dataSvolgimento.getTime());

            SessioneAllenamento nuovaSessione = new SessioneAllenamento(titolo, descrizione, durataPrevista, dataSQL);

            nuovaSessione.setAtleta(atleta);
            atleta.aggiungiSessione(nuovaSessione);

            // Per evitare che l'ID rimanga null nell'oggetto locale a causa del disallineamento della cache di sessione,
            // salviamo l'atleta (che fa cascade sulla sessione) ed estraiamo l'istanza aggiornata dal database.
            db.aggiorna(atleta);

            // TRUCCO DI SICUREZZA: Recuperiamo l'ultimo elemento della lista delle sessioni dell'atleta.
            // Se Hibernate ha sincronizzato lo stato tramite il cascade, l'ultimo elemento
            // della collezione conterrà l'ID autoincrementato aggiornato.
            List<SessioneAllenamento> sessioni = atleta.getSessioniAllenamento();
            if (sessioni != null && !sessioni.isEmpty()) {
                SessioneAllenamento sessioneAggiornata = sessioni.get(sessioni.size() - 1);

                // Se per qualche motivo l'ID è ancora null (es. a causa del pattern detached del GestorePersistenza),
                // proviamo a forzare un salvataggio/aggiornamento diretto sulla sessione per blindare il risultato.
                if (sessioneAggiornata.getIdSessione() == null) {
                    db.salva(nuovaSessione);
                    return nuovaSessione;
                }

                return sessioneAggiornata;
            }

            return nuovaSessione;
        }
        return null;
    }
    public SessioneAllenamento getSessioneById(Long idSessione) {
        if (idSessione == null) {
            return null;
        }
        // Sostituisci 'db' con il nome della tua variabile interna al Facade per il GestorePersistenza/EntityManager
        return db.trovaPerId(SessioneAllenamento.class, idSessione);
    }

    public void aggiungiEsercizioASessione(Long idAllenatore, Long idSessione, String nome, String descrizione, int ripetizioniPreviste, int durataPrevista) {
        SessioneAllenamento sessione = db.trovaPerId(SessioneAllenamento.class, idSessione);
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);

        if (sessione != null && allenatore != null && allenatore.haAtletaAssociato(sessione.getAtleta())) {
            Esercizio nuovoEsercizio = new Esercizio(nome, descrizione, ripetizioniPreviste, durataPrevista);
            sessione.aggiungiEsercizio(nuovoEsercizio);
            db.aggiorna(sessione);
        }
    }

    public void registraEsecuzioneEsercizio(Long idAtleta, Long idEsercizio, int ripetizioniEffettive, int tempoImpiegato, String nota) {
        Esercizio esercizio = db.trovaPerId(Esercizio.class, idEsercizio);
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

        if (esercizio != null && atleta != null && atleta.getSessioniAllenamento().contains(esercizio.getSessione())) {
            esercizio.registraEsecuzione(ripetizioniEffettive, tempoImpiegato, nota);
            db.aggiorna(esercizio);
        }
    }
    public List<Esercizio> getEserciziSessione(Long idSessione) {
        // 1. Recuperiamo la sessione dal database tramite il suo ID
        SessioneAllenamento sessione = db.trovaPerId(SessioneAllenamento.class, idSessione);

        if (sessione != null) {
            // 2. Usiamo il getter che hai appena definito nell'entità Sessione
            return sessione.getEsercizi();
        }

        // Se la sessione non esiste, ritorniamo una lista vuota per evitare NullPointerException
        return new ArrayList<>();
    }

    public boolean esisteCodiceAllenatore(String codice) {
        if (codice == null || codice.trim().isEmpty()) {
            return false;
        }
        List<Allenatore> allenatori = db.cercaPerCampo(Allenatore.class, "codicePerAssociare", codice.trim());
        return allenatori != null && !allenatori.isEmpty();
    }

    public List<Atleta> getAtletiAssociati(Long idAllenatore) {
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);
        if (allenatore != null) {
            return allenatore.getAtletiAssociati();
        }
        return new java.util.ArrayList<>();
    }

    // METODI PER LA DASHBOARD

    public Map<String, Integer> getIndicatoriAggregati(Long idAllenatore) {
        Map<String, Integer> indicatori = new java.util.HashMap<>();
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);

        if (allenatore != null) {
            List<Atleta> atleti = allenatore.getAtletiAssociati();

            int totaleAtleti = atleti.size();
            int sessioniTotaliAssegnate = 0;
            int sessioniCompletate = 0;
            int sessioniInCorso = 0; // Nuovo contatore

            for (Atleta a : atleti) {
                for (SessioneAllenamento s : a.getSessioniAllenamento()) {
                    sessioniTotaliAssegnate++;

                    // Controllo basato sullo stato della sessione
                    if (s.getStato() == StatoSessione.COMPLETATA) {
                        sessioniCompletate++;
                    } else if (s.getStato() == StatoSessione.IN_CORSO) {
                        sessioniInCorso++;
                    }
                }
            }

            indicatori.put("Totale Atleti", totaleAtleti);
            indicatori.put("Sessioni Assegnate", sessioniTotaliAssegnate);
            indicatori.put("Sessioni Completate", sessioniCompletate);
            indicatori.put("Sessioni In Corso", sessioniInCorso); // Aggiunta alla mappa
        }

        return indicatori;
    }

    public Map<Date, Integer> getEvoluzioneAtleta(Long idAllenatore, Long idAtleta) {
        // Usiamo TreeMap così le date sono ordinate automaticamente in ordine cronologico
        Map<Date, Integer> andamentoStorico = new java.util.TreeMap<>();

        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);

        // Controllo di sicurezza: l'atleta deve esistere e deve essere associato a questo coach
        if (atleta != null && allenatore != null && allenatore.haAtletaAssociato(atleta)) {

            for (SessioneAllenamento s : atleta.getSessioniAllenamento()) {
                // Calcoliamo l'evoluzione solo sulle sessioni effettivamente terminate
                if (s.isCompletata() && s.getDataSvolgimento() != null) {

                    int volumeRipetizioniSessione = 0;
                    for (Esercizio e : s.getEsercizi()) {
                        volumeRipetizioniSessione += e.getRipetizioniEffettive();
                    }

                    // Se ci sono più sessioni nello stesso giorno, sommiamo i volumi
                    int volumeEsistente = andamentoStorico.getOrDefault(s.getDataSvolgimento(), 0);
                    andamentoStorico.put(s.getDataSvolgimento(), volumeEsistente + volumeRipetizioniSessione);
                }
            }
        }

        return andamentoStorico;
    }

    public Map<String, Double> generaConfrontoPrevistoEffettivo(Long idAllenatore, Long idAtleta) {
        Map<String, Double> scostamenti = new java.util.HashMap<>();

        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);

        if (atleta != null && allenatore != null && allenatore.haAtletaAssociato(atleta)) {

            int ripPrevisteTotali = 0;
            int ripEffettiveTotali = 0;
            int tempoPrevistoTotale = 0;
            int tempoEffettivoTotale = 0;

            // Raccogliamo i dati di tutte le sessioni completate
            for (SessioneAllenamento s : atleta.getSessioniAllenamento()) {
                if (s.isCompletata()) {
                    for (Esercizio e : s.getEsercizi()) {
                        ripPrevisteTotali += e.getRipetizioniPreviste();
                        ripEffettiveTotali += e.getRipetizioniEffettive();
                        tempoPrevistoTotale += e.getDurataPrevista();
                        tempoEffettivoTotale += e.getTempoImpiegato();
                    }
                }
            }

            // Calcolo percentuali di completamento (evitando divisioni per zero)
            if (ripPrevisteTotali > 0) {
                double percRipetizioni = ((double) ripEffettiveTotali / ripPrevisteTotali) * 100;
                scostamenti.put("Tasso Completamento Ripetizioni (%)", percRipetizioni);
            }

            if (tempoPrevistoTotale > 0) {
                double percTempo = ((double) tempoEffettivoTotale / tempoPrevistoTotale) * 100;
                scostamenti.put("Tasso Rispetto Tempi (%)", percTempo);
            }
        }

        return scostamenti;
    }

    public List<SessioneAllenamento> getSessioniAtletiAssociati(Long idAllenatore) {
        List<SessioneAllenamento> tutteLeSessioni = new java.util.ArrayList<>();
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);

        if (allenatore != null) {
            // Scorriamo tutti gli atleti e accumuliamo le loro sessioni in un'unica lista
            for (Atleta a : allenatore.getAtletiAssociati()) {
                tutteLeSessioni.addAll(a.getSessioniAllenamento());
            }
        }
        return tutteLeSessioni;
    }

    // Recupera tutte le sessioni associate a un singolo atleta
    public List<SessioneAllenamento> getSessioniAtleta(Long idAtleta) {
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

        if (atleta != null) {
            // Restituisce la lista completa delle sessioni dell'atleta.
            // (Il filtro per stato verrà poi applicato dalla GUI o dal Controller)
            return atleta.getSessioniAllenamento();
        }

        return new java.util.ArrayList<>(); // Ritorna lista vuota se l'atleta non esiste
    }
    public List<SessioneAllenamento> filtraSessioniAtleta(Long idAtleta, String stato, String keywordTitolo, Date dataEsatta) {
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

        if (atleta == null) {
            return new java.util.ArrayList<>();
        }

        List<SessioneAllenamento> tutteLeSessioni = atleta.getSessioniAllenamento();

        // Usa gli Stream di Java per un filtraggio elegante e pulito
        return tutteLeSessioni.stream()
                // Filtro 1: Stato (ignora se null o se vale "Tutte le sessioni")
                .filter(s -> stato == null || stato.equals("Tutte le sessioni") || s.getStato().equalsIgnoreCase(stato))

                // Filtro 2: Titolo (ignora se null o vuoto)
                .filter(s -> keywordTitolo == null || keywordTitolo.trim().isEmpty() ||
                        (s.getTitolo() != null && s.getTitolo().toLowerCase().contains(keywordTitolo.trim().toLowerCase())))

                // Filtro 3: Data (ignora se null) - Attenzione: confronta usando metodi sicuri per le date
                .filter(s -> dataEsatta == null ||
                        (s.getDataSvolgimento() != null && isStessoGiorno(s.getDataSvolgimento(), dataEsatta)))

                // Raccoglie i risultati finali in una nuova lista
                .collect(Collectors.toList());
    }

    // Metodo di supporto (privato) per confrontare se due date sono lo stesso giorno
    // ignorando ore/minuti/secondi
    private boolean isStessoGiorno(Date data1, Date data2) {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTime(data1);
        cal2.setTime(data2);
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);
    }
}