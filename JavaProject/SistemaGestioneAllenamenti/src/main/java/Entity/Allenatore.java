package Entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "allenatori") // Diciamo al DB come si chiamerà la tabella
public class Allenatore extends Utente {

    @Id // È la chiave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Il DB la incrementa in automatico
    @Column(name = "id_allenatore")
    private Long idAllenatore;

    @Column(name = "codice_associazione", unique = true) // Aggiungiamo 'unique' per evitare codici doppi nel DB!
    private String codicePerAssociare;

    // Relazione 1 a Molti: 1 Allenatore ha N Atleti.
    // 'mappedBy' significa che la chiave esterna sta nell'Atleta (nella variabile chiamata "allenatoreAssociato")
    @OneToMany(mappedBy = "allenatoreAssociato", cascade = CascadeType.ALL)
    private List<Atleta> atletiAssociati;

    // =========================================================
    // COSTRUTTORI
    // =========================================================

    // 1. Costruttore vuoto obbligatorio per JPA
    protected Allenatore() {
        super();
        this.atletiAssociati = new ArrayList<>();
    }

    // 2. Costruttore per la Registrazione (SENZA ID)
    public Allenatore(String codicePerAssociare, String nome, String cognome, String email, String password, String disciplinaPrevalente) {
        super(nome, cognome, email, password, disciplinaPrevalente);
        this.codicePerAssociare = codicePerAssociare;
        this.atletiAssociati = new ArrayList<>();
    }

    // 3. Costruttore completo (CON ID, per quando ricarichi i dati)
    public Allenatore(Long idAllenatore, String codicePerAssociare, String nome, String cognome, String email, String password, String disciplinaPrevalente) {
        super(nome, cognome, email, password, disciplinaPrevalente);
        this.idAllenatore = idAllenatore;
        this.codicePerAssociare = codicePerAssociare;
        this.atletiAssociati = new ArrayList<>();
    } // <--- MANCAVA QUESTA PARENTESI GRAFFA QUI!

    // =========================================================
    // METODI
    // =========================================================

    public boolean aggiungiAtleta(Atleta atleta) {
        if (!atletiAssociati.contains(atleta)) {
            atletiAssociati.add(atleta);
            atleta.associaAllenatore(this); // Associazione bidirezionale
            return true;
        }
        return false;
    }

    public boolean rimuoviAtleta(Atleta atleta) {
        if (atletiAssociati.remove(atleta)) {
            atleta.disassociaAllenatore();
            return true;
        }
        return false;
    }

    public boolean verificaCodiceAssociazione(String codice) {
        return this.codicePerAssociare.equals(codice);
    }

    public boolean haAtletaAssociato(Atleta atleta) {
        return this.atletiAssociati.contains(atleta);
    }

    // Getter e Setter
    public Long getIdAllenatore() { return idAllenatore; }
    public String getCodicePerAssociare() { return codicePerAssociare; }
    public List<Atleta> getAtletiAssociati() { return atletiAssociati; }
}