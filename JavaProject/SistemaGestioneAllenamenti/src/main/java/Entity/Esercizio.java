package Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "esercizi")
public class Esercizio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_esercizio")
    private Long idEsercizio;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT") // TEXT permette di inserire testi molto lunghi
    private String descrizione;

    @Column(name = "ripetizioni_previste")
    private int ripetizioniPreviste;

    @Column(name = "durata_prevista")
    private int durataPrevista; // in minuti

    // =========================================================
    // Attributi di performance (Prestazione) compilati dall'Atleta
    // =========================================================
    @Column(name = "ripetizioni_effettive")
    private int ripetizioniEffettive;

    @Column(name = "tempo_impiegato")
    private int tempoImpiegato;

    @Column(name = "nota_testuale", columnDefinition = "TEXT")
    private String notaTestuale;

    // =========================================================
    // RELAZIONI
    // =========================================================
    // Relazione Molti a 1: Molti Esercizi appartengono a 1 Sessione
    @ManyToOne
    @JoinColumn(name = "id_sessione")
    private SessioneAllenamento sessione;

    // =========================================================
    // COSTRUTTORI
    // =========================================================

    // 1. Costruttore vuoto obbligatorio per JPA
    protected Esercizio() {
    }

    // 2. Costruttore base per la creazione (senza ID e prestazioni)
    public Esercizio(String nome, String descrizione, int ripetizioniPreviste, int durataPrevista) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.ripetizioniPreviste = ripetizioniPreviste;
        this.durataPrevista = durataPrevista;
    }

    // 3. Costruttore completo (con ID, utile per i test o ricaricamento)
    public Esercizio(Long idEsercizio, String nome, String descrizione, int ripetizioniPreviste, int durataPrevista) {
        this.idEsercizio = idEsercizio;
        this.nome = nome;
        this.descrizione = descrizione;
        this.ripetizioniPreviste = ripetizioniPreviste;
        this.durataPrevista = durataPrevista;
    }

    // =========================================================
    // METODI
    // =========================================================

    // Funzionalità richiesta dal requisito 12 (Registrazione risultati)
    public void registraEsecuzione(int ripetizioniEffettive, int tempoImpiegato, String notaTestuale) {
        this.ripetizioniEffettive = ripetizioniEffettive;
        this.tempoImpiegato = tempoImpiegato;
        this.notaTestuale = notaTestuale;
    }

    // =========================================================
    // GETTER E SETTER
    // =========================================================

    public Long getIdEsercizio() { return idEsercizio; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public int getRipetizioniPreviste() { return ripetizioniPreviste; }
    public void setRipetizioniPreviste(int ripetizioniPreviste) { this.ripetizioniPreviste = ripetizioniPreviste; }

    public int getDurataPrevista() { return durataPrevista; }
    public void setDurataPrevista(int durataPrevista) { this.durataPrevista = durataPrevista; }

    public int getRipetizioniEffettive() { return ripetizioniEffettive; }
    public int getTempoImpiegato() { return tempoImpiegato; }
    public String getNotaTestuale() { return notaTestuale; }

    // Getter e Setter per la relazione con la sessione
    public SessioneAllenamento getSessione() { return sessione; }
    public void setSessione(SessioneAllenamento sessione) { this.sessione = sessione; }
}