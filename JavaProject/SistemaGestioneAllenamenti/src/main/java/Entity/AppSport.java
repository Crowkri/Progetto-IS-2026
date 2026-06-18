package Entity;

import java.util.*;

import Database.GestorePersistenza;

import java.util.stream.Collectors;

public class AppSport {

    private GestorePersistenza db;

    public AppSport() {
        this.db = new GestorePersistenza();
    }


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

        // Associazione immediata se codice presente
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
        Allenatore nuovoAllenatore = new Allenatore(codiceAssociazione, nome, cognome, email, password, disciplina);
        db.salva(nuovoAllenatore);

        return nuovoAllenatore;
    }

    public void modificaProfiloAtleta(Long idAllenatore, Long idAtleta, String disciplina, String livelloEsperienza, String obiettiviSportivi) {
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

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
            return null;
        }

        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);
        if (atleta == null) {
            throw new IllegalArgumentException("Atleta non trovato nel sistema.");
        }

        List<Allenatore> allenatori = db.cercaPerCampo(Allenatore.class, "codicePerAssociare", codice.trim());

        if (allenatori != null && !allenatori.isEmpty()) {
            Allenatore allenatore = allenatori.get(0);

            boolean associato = allenatore.aggiungiAtleta(atleta);

            if (associato) {
                db.aggiorna(allenatore);
                db.aggiorna(atleta);
            }

            return allenatore;
        }

        return null;
    }
    public boolean associazioneDiretta(Long idAllenatore, Long idAtleta) {
        if (idAllenatore == null || idAtleta == null) {
            return false;
        }

        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

        if (allenatore == null) {
            throw new IllegalArgumentException("Errore: Allenatore non trovato nel sistema.");
        }
        if (atleta == null) {
            throw new IllegalArgumentException("Errore: Nessun atleta trovato con questo ID.");
        }

        boolean associato = allenatore.aggiungiAtleta(atleta);

        if (associato) {
            db.aggiorna(allenatore);
            db.aggiorna(atleta);
            return true;
        }

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


    public SessioneAllenamento creaNuovaSessione(Long idAllenatore, Long idAtleta, String titolo, String descrizione, int durataPrevista, Date dataSvolgimento) {
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

        if (allenatore != null && atleta != null && allenatore.haAtletaAssociato(atleta)) {

            // FIX DATA: Convertiamo java.util.Date in java.sql.Date
            java.sql.Date dataSQL = new java.sql.Date(dataSvolgimento.getTime());

            SessioneAllenamento nuovaSessione = new SessioneAllenamento(titolo, descrizione, durataPrevista, dataSQL);

            nuovaSessione.setAtleta(atleta);
            atleta.aggiungiSessione(nuovaSessione);

            db.aggiorna(atleta);

            List<SessioneAllenamento> sessioni = atleta.getSessioniAllenamento();
            if (sessioni != null && !sessioni.isEmpty()) {
                SessioneAllenamento sessioneAggiornata = sessioni.get(sessioni.size() - 1);

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
        SessioneAllenamento sessione = db.trovaPerId(SessioneAllenamento.class, idSessione);

        if (sessione != null) {
            return sessione.getEsercizi();
        }

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


    public Map<String, Integer> getIndicatoriAggregati(Long idAllenatore) {
        Map<String, Integer> indicatori = new java.util.HashMap<>();
        Allenatore allenatore = db.trovaPerId(Allenatore.class, idAllenatore);

        if (allenatore != null) {
            List<Atleta> atleti = allenatore.getAtletiAssociati();

            int totaleAtleti = atleti.size();
            int sessioniTotaliAssegnate = 0;
            int sessioniCompletate = 0;
            int sessioniInCorso = 0;

            for (Atleta a : atleti) {
                for (SessioneAllenamento s : a.getSessioniAllenamento()) {
                    sessioniTotaliAssegnate++;

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

        if (atleta != null && allenatore != null && allenatore.haAtletaAssociato(atleta)) {

            for (SessioneAllenamento s : atleta.getSessioniAllenamento()) {
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
            for (Atleta a : allenatore.getAtletiAssociati()) {
                tutteLeSessioni.addAll(a.getSessioniAllenamento());
            }
        }
        return tutteLeSessioni;
    }

    public List<SessioneAllenamento> getSessioniAtleta(Long idAtleta) {
        Atleta atleta = db.trovaPerId(Atleta.class, idAtleta);

        if (atleta != null) {
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

        // Uso degli Stream di Java per un filtraggio ottimizzato
        return tutteLeSessioni.stream()
                // Filtro 1: Stato
                .filter(s -> stato == null || stato.equals("Tutte le sessioni") || s.getStato().equalsIgnoreCase(stato))

                // Filtro 2: Titolo
                .filter(s -> keywordTitolo == null || keywordTitolo.trim().isEmpty() ||
                        (s.getTitolo() != null && s.getTitolo().toLowerCase().contains(keywordTitolo.trim().toLowerCase())))

                // Filtro 3: Data
                .filter(s -> dataEsatta == null ||
                        (s.getDataSvolgimento() != null && isStessoGiorno(s.getDataSvolgimento(), dataEsatta)))

                // Raccoglie i risultati finali in una nuova lista
                .collect(Collectors.toList());
    }

    private boolean isStessoGiorno(Date data1, Date data2) {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTime(data1);
        cal2.setTime(data2);
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);
    }
}