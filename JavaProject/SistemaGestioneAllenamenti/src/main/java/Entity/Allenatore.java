package Entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "allenatori")
public class Allenatore extends Utente {

    @Id //Chiave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_allenatore")
    private Long idAllenatore;

    @Column(name = "codice_associazione", unique = true)
    private String codicePerAssociare;

    // 'mappedBy' significa che la chiave esterna sta nell'Atleta (nella variabile chiamata "allenatoreAssociato")
    @OneToMany(mappedBy = "allenatoreAssociato",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Atleta> atletiAssociati;


    protected Allenatore() {
        super();
        this.atletiAssociati = new ArrayList<>();
    }

    public Allenatore(String codicePerAssociare, String nome, String cognome, String email, String password, String disciplinaPrevalente) {
        super(nome, cognome, email, password, disciplinaPrevalente);
        this.codicePerAssociare = codicePerAssociare;
        this.atletiAssociati = new ArrayList<>();
    }

    public Allenatore(Long idAllenatore, String codicePerAssociare, String nome, String cognome, String email, String password, String disciplinaPrevalente) {
        super(nome, cognome, email, password, disciplinaPrevalente);
        this.idAllenatore = idAllenatore;
        this.codicePerAssociare = codicePerAssociare;
        this.atletiAssociati = new ArrayList<>();
    }


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