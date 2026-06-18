package Entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "atleti")
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
    @Embedded
    private ProfiloAtleta profilo;

    // Relazione 1 a Molti: 1 Atleta ha N Sessioni
    // orphanRemoval = true significa che se togliamo una sessione dalla lista, viene cancellata dal DB
    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SessioneAllenamento> sessioniAllenamento;


    protected Atleta() {
        super();
        this.sessioniAllenamento = new ArrayList<>();
    }

    public Atleta(String nome, String cognome, String email, String password, String disciplinaPrevalente) {
        super(nome, cognome, email, password, disciplinaPrevalente);
        this.sessioniAllenamento = new ArrayList<>();
    }

    public Atleta(Long idAtleta, String nome, String cognome, String email, String password, String disciplinaPrevalente) {
        super(nome, cognome, email, password, disciplinaPrevalente);
        this.idAtleta = idAtleta;
        this.sessioniAllenamento = new ArrayList<>();
    }


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

    public boolean haAllenatoreAssociato(Allenatore allenatore) {
        return this.allenatoreAssociato != null && this.allenatoreAssociato.equals(allenatore);
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Atleta)) return false;

        Atleta atleta = (Atleta) o;

        return idAtleta != null && idAtleta.equals(atleta.getIdAtleta());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    public Long getIdAtleta() { return idAtleta; }
    public Allenatore getAllenatoreAssociato() { return allenatoreAssociato; }
    public ProfiloAtleta getProfilo() { return profilo; }
    public List<SessioneAllenamento> getSessioniAllenamento() { return sessioniAllenamento; }
}