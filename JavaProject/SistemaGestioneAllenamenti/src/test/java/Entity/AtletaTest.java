package Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class AtletaTest {

    private Atleta atleta;
    private SessioneAllenamento sessione;
    private ProfiloAtleta profilo;

    @BeforeEach
    void setUp() {
        atleta = new Atleta("Giulia", "Bianchi", "giulia@email.com", "pass789", "Nuoto");
        sessione = new SessioneAllenamento("Allenamento Dorso", "Frazionato", 45, new Date());
        profilo = new ProfiloAtleta("Nuoto", "Intermedio", "Migliorare i tempi sui 100m");
    }

    @Test
    void testAggiungiERimuoviSessione() {
        // Verifica iniziale
        assertTrue(atleta.getSessioniAllenamento().isEmpty(), "La lista delle sessioni dovrebbe essere inizialmente vuota");

        // Esecuzione: Aggiunta
        atleta.aggiungiSessione(sessione);

        // Verifica: Aggiunta
        assertEquals(1, atleta.getSessioniAllenamento().size(), "L'atleta deve avere esattamente 1 sessione");
        assertTrue(atleta.getSessioniAllenamento().contains(sessione), "La sessione corretta deve essere presente");

        // Esecuzione: Rimozione
        boolean rimosso = atleta.rimuoviSessione(sessione);

        // Verifica: Rimozione
        assertTrue(rimosso, "Il metodo rimuoviSessione deve restituire true");
        assertTrue(atleta.getSessioniAllenamento().isEmpty(), "La lista delle sessioni deve tornare vuota");
    }

    @Test
    void testImpostaProfilo() {
        // Verifica stato iniziale
        assertNull(atleta.getProfilo(), "Inizialmente il profilo dell'atleta dovrebbe essere null");

        // Esecuzione
        atleta.impostaProfilo(profilo);

        // Verifica
        assertNotNull(atleta.getProfilo(), "Il profilo non deve essere più null");
        assertEquals("Migliorare i tempi sui 100m", atleta.getProfilo().getObiettiviSportivi());
    }
}