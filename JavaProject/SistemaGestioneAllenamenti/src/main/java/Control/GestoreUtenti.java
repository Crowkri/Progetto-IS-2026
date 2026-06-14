package Control;

import Entity.Atleta;
import Entity.Utente;
import Entity.Allenatore;

import java.util.List;

public class GestoreUtenti {

    private AppSport facade;

    public GestoreUtenti(AppSport facade) {
        // Nessun accesso al database, si appoggia solo al Facade
        this.facade = facade;
    }

    public Utente autenticaUtente(String email, String password) {
        // Validazione sintattica dell'input
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Credenziali non valide.");
        }
        return facade.autenticaUtente(email, password);
    }

    public Atleta registraAtleta(String nome, String cognome, String email, String password, String disciplina, String codiceAssociazione) {
        if (nome == null || email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Dati obbligatori mancanti o errati.");
        }
        // Il controller delega tutta la complessità al Facade
        return facade.registraAtleta(nome, cognome, email, password, disciplina, codiceAssociazione);
    }


    public void modificaProfiloAtleta(Long idAllenatore, Long idAtleta, String disciplina, String livelloEsperienza, String obiettiviSportivi) {
        if (idAllenatore == null || idAtleta == null) {
            throw new IllegalArgumentException("ID utenti mancanti.");
        }
        facade.modificaProfiloAtleta(idAllenatore, idAtleta, disciplina, livelloEsperienza, obiettiviSportivi);
    }
    public Allenatore registraAllenatore(String nome, String cognome, String email, String password, String disciplina, String codiceAssociazione) {
        // 1. Validazione sintattica e formale dell'input (Logica di Presentazione)
        if (nome == null || nome.isBlank() || email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Dati anagrafici mancanti o formato email errato.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La password deve contenere almeno 6 caratteri.");
        }
        if (codiceAssociazione == null || codiceAssociazione.isBlank()) {
            throw new IllegalArgumentException("Il codice univoco di associazione è obbligatorio per registrarsi come Allenatore.");
        }

        // 2. Delega dell'operazione vera e propria al Facade
        return facade.registraAllenatore(nome, cognome, email, password, disciplina, codiceAssociazione);
    }
    public boolean esisteCodiceAllenatore(String codice) {
        // Controllo di sicurezza preliminare sul parametro
        if (codice == null || codice.trim().isEmpty()) {
            return false;
        }
        // Delega alla Facade
        return facade.esisteCodiceAllenatore(codice.trim());
    }
    public List<Atleta> getAtletiAssociati(Long idAllenatore) {
        // Validazione base
        if (idAllenatore == null) {
            throw new IllegalArgumentException("Errore: ID Allenatore non fornito.");
        }

        // Delega al Facade
        return facade.getAtletiAssociati(idAllenatore);
    }
    public Allenatore associaConCodice(Long idAtleta, String codice) {
        return facade.associaConCodice(idAtleta, codice);
    }
    public boolean associazioneDiretta(Long idAllenatore, Long idAtleta) {
        try {
            return facade.associazioneDiretta(idAllenatore, idAtleta);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    public void dissociaAllenatore(Long idAtleta) {
        facade.dissociaAllenatore(idAtleta);
    }

}