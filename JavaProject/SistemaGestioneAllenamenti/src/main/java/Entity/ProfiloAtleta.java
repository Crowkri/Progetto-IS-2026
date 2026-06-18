package Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProfiloAtleta {

    @Column(name = "disciplina_praticata")
    private String disciplinaPraticata;

    @Column(name = "livello_esperienza")
    private String livelloEsperienza;

    @Column(name = "obiettivi_sportivi", columnDefinition = "TEXT")
    private String obiettiviSportivi;


    protected ProfiloAtleta() {
    }

    public ProfiloAtleta(String disciplinaPraticata, String livelloEsperienza, String obiettiviSportivi) {
        this.disciplinaPraticata = disciplinaPraticata;
        this.livelloEsperienza = livelloEsperienza;
        this.obiettiviSportivi = obiettiviSportivi;
    }


    public void aggiornaObiettivi(String nuoviObiettivi) {
        this.obiettiviSportivi = nuoviObiettivi;
    }

    public void avanzaLivelloEsperienza(String nuovoLivello) {
        this.livelloEsperienza = nuovoLivello;
    }

    public void cambiaDisciplina(String nuovaDisciplina) {
        this.disciplinaPraticata = nuovaDisciplina;
    }


    public String getDisciplinaPraticata() {
        return disciplinaPraticata; }
    public void setDisciplinaPraticata(String disciplinaPraticata) {
        this.disciplinaPraticata = disciplinaPraticata; }

    public String getLivelloEsperienza() {
        return livelloEsperienza; }

    public void setLivelloEsperienza(String livelloEsperienza) {
        this.livelloEsperienza = livelloEsperienza; }

    public String getObiettiviSportivi() {
        return obiettiviSportivi; }
    public void setObiettiviSportivi(String obiettiviSportivi) {
        this.obiettiviSportivi = obiettiviSportivi; }
}