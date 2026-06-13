package Control;

import java.util.Date;
import java.util.List;
import Database.GestorePersistenza;
import Entity.Allenatore;
import Entity.Atleta;
import Entity.Esercizio;
import Entity.ProfiloAtleta;
import Entity.SessioneAllenamento;
import Entity.Utente;
import Entity.StatoSessione;
import java.util.Map;
import java.util.Date;
import java.util.List;

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
}