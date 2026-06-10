package Entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass // Dice a JPA di ereditare queste colonne nelle tabelle figlie
public abstract class Utente {

    @Column(nullable = false) // Il nome è obbligatorio
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(nullable = false, unique = true) // L'email deve essere unica e obbligatoria
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "disciplina_prevalente")
    private String disciplinaPrevalente;

    // COSTRUTTORI

    // 1. Costruttore vuoto
    protected Utente() {
    }

    // 2. Costruttore completo per la creazione
    public Utente(String nome, String cognome, String email, String password, String disciplinaPrevalente) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.disciplinaPrevalente = disciplinaPrevalente;
    }

    // =========================================================
    // GETTER E SETTER
    // =========================================================

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDisciplinaPrevalente() { return disciplinaPrevalente; }
    public void setDisciplinaPrevalente(String disciplinaPrevalente) { this.disciplinaPrevalente = disciplinaPrevalente; }
}