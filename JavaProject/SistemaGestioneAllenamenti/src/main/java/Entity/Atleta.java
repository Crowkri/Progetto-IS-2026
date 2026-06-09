package Entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "atleti") // Nome della tabella nel database
public class Atleta extends Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_atleta")
    private Long idAtleta;

    // Relazione Molti a 1: Molti Atleti possono avere 1 Allenatore
    // @JoinColumn crea la chiave esterna (Foreign Key) fisica nel database
    @ManyToOne
    @JoinColumn(name = "id_allenatore")
    private Allenatore allenatoreAssociato;

    // @Embedded "fonde" i campi di ProfiloAtleta direttamente in questa tabella
    // (nota: la classe ProfiloAtleta dovrà avere l'annotazione @Embeddable)
    @Embedded
    private ProfiloAtleta profilo;

    // Relazione 1 a Molti: 1 Atleta ha N Sessioni
    // orphanRemoval = true significa che se togliamo una sessione dalla lista, viene cancellata dal DB
    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessioneAllenamento> sessioniAllenamento;

    // =========================================================
    // COSTRUTTORI
    // =========================================================

    // 1. Costruttore vuoto obbligatorio per JPA
    protected Atleta() {
        super();
        this.sessioniAllenamento = new ArrayList<>();
    }

    // 2. Costruttore per NUOVE registrazioni (SENZA ID)
    public Atleta(String nome, String cognome, String email, String password, String disciplinaPrevalente) {
        super(nome, cognome, email, password, disciplinaPrevalente);
        this.sessioniAllenamento = new ArrayList<>();
    }

    // 3. Costruttore completo (CON ID, usato per ricaricare i dati)
    public Atleta(Long idAtleta, String nome, String cognome, String email, String password, String disciplinaPrevalente) {
        super(nome, cognome, email, password, disciplinaPrevalente);
        this.idAtleta = idAtleta;
        this.sessioniAllenamento = new ArrayList<>();
    }

    // =========================================================
    // METODI DI LOGICA
    // =========================================================

    public void impostaProfilo(ProfiloAtleta profilo) {
        this.profilo = profilo;
    }

    public void associaAllenatore(Allenatore allenatore) {
        this.allenatoreAssociato = allenatore;
    }

    public void disassociaAllenatore() {
        this.allenatoreAssociato = null;
    }

    public void aggiungiSessione(SessioneAllenamento sessione) {
        this.sessioniAllenamento.add(sessione);
    }

    public boolean rimuoviSessione(SessioneAllenamento sessione) {
        return this.sessioniAllenamento.remove(sessione);
    }

    public boolean haAllenatoreAssociato(Long idAllenatore) {
        return this.allenatoreAssociato != null && this.allenatoreAssociato.getIdAllenatore().equals(idAllenatore);
    }

    // =========================================================
    // GETTER E SETTER
    // =========================================================

    public Long getIdAtleta() { return idAtleta; }
    public Allenatore getAllenatoreAssociato() { return allenatoreAssociato; }
    public ProfiloAtleta getProfilo() { return profilo; }
    public List<SessioneAllenamento> getSessioniAllenamento() { return sessioniAllenamento; }
}