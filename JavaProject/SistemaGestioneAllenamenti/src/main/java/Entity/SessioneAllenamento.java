package Entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessioni_allenamento")
public class SessioneAllenamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sessione")
    private Long idSessione;

    @Column(nullable = false)
    private String titolo;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(name = "durata_prevista")
    private int durataPrevista; // in minuti

    @Temporal(TemporalType.DATE)
    @Column(name = "data_svolgimento")
    private Date dataSvolgimento;

    @Enumerated(EnumType.STRING) // Salva l'enum nel DB come stringa leggibile
    @Column(name = "stato")
    private StatoSessione stato;


    // Relazione Molti a 1: Molte Sessioni appartengono a 1 Atleta
    @ManyToOne
    @JoinColumn(name = "id_atleta")
    private Atleta atleta;

    // Relazione 1 a Molti: 1 Sessione ha N Esercizi
    @OneToMany(mappedBy = "sessione", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Esercizio> esercizi;


    protected SessioneAllenamento() {
        this.esercizi = new ArrayList<>();
    }

    public SessioneAllenamento(String titolo, String descrizione, int durataPrevista, Date dataSvolgimento) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.durataPrevista = durataPrevista;
        this.dataSvolgimento = dataSvolgimento;
        this.stato = StatoSessione.ASSEGNATA; // Stato iniziale di default
        this.esercizi = new ArrayList<>();
    }

    public SessioneAllenamento(Long idSessione, String titolo, String descrizione, int durataPrevista, Date dataSvolgimento) {
        this.idSessione = idSessione;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.durataPrevista = durataPrevista;
        this.dataSvolgimento = dataSvolgimento;
        this.stato = StatoSessione.ASSEGNATA;
        this.esercizi = new ArrayList<>();
    }


    public void aggiungiEsercizio(Esercizio esercizio) {
        this.esercizi.add(esercizio);
        esercizio.setSessione(this);
    }

    public boolean rimuoviEsercizio(Esercizio esercizio) {
        if (this.esercizi.remove(esercizio)) {
            esercizio.setSessione(null);
            return true;
        }
        return false;
    }

    public void completaSessione() {
        this.stato = StatoSessione.COMPLETATA;
    }

    public boolean isCompletata() {
        return this.stato == StatoSessione.COMPLETATA;
    }

    public int calcolaDurataTotaleEffettiva() {
        int totale = 0;
        for (Esercizio e : esercizi) {
            totale += e.getTempoImpiegato();
        }
        return totale;
    }


    public Long getIdSessione() { return idSessione; }

    public String getTitolo() {
        return titolo; }
    public void setTitolo(String titolo) {
        this.titolo = titolo; }

    public String getDescrizione() {
        return descrizione; }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione; }

    public int getDurataPrevista() {
        return durataPrevista; }
    public void setDurataPrevista(int durataPrevista) {
        this.durataPrevista = durataPrevista; }

    public Date getDataSvolgimento() {
        return dataSvolgimento; }
    public void setDataSvolgimento(Date dataSvolgimento) {
        this.dataSvolgimento = dataSvolgimento; }

    public StatoSessione getStato() {
        return stato; }
    public void setStato(StatoSessione stato) {
        this.stato = stato; }

    public List<Esercizio> getEsercizi() {
        return esercizi; }

    public Atleta getAtleta() {
        return atleta; }
    public void setAtleta(Atleta atleta) {
        this.atleta = atleta; }
}