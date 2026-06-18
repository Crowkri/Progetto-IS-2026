package Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EsercizioTest {

    private Esercizio esercizio;

    @BeforeEach
    void setUp() {
        // Creiamo un esercizio con i valori previsti dall'allenatore
        esercizio = new Esercizio("Plank", "Mantenere la posizione", 1, 3); // 1 ripetizione da 3 minuti
    }

    @Test
    void testRegistraEsecuzione() {
        // Verifica stato iniziale (i valori effettivi dovrebbero essere 0 o null dato che non sono stati valorizzati nel costruttore base)
        assertNull(esercizio.getNotaTestuale(), "La nota testuale iniziale deve essere null");
        // Nota: se non inizializzi a 0 nella classe Entity, questi Integer saranno null.

        // Esecuzione: L'atleta compila l'esercizio
        esercizio.registraEsecuzione(1, 2, "Cedimento negli ultimi 30 secondi");

        // Verifica dell'aggiornamento
        assertEquals(1, esercizio.getRipetizioniEffettive(), "Le ripetizioni effettive devono essere aggiornate");
        assertEquals(2, esercizio.getTempoImpiegato(), "Il tempo impiegato effettivo deve essere 2 minuti");
        assertEquals("Cedimento negli ultimi 30 secondi", esercizio.getNotaTestuale(), "La nota deve essere salvata correttamente");
    }
}