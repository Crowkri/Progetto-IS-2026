package Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class SessioneAllenamentoTest {

    private SessioneAllenamento sessione;

    @BeforeEach
    void setUp() {
        sessione = new SessioneAllenamento("Gambe e Core", "Focus squat", 60, new Date());
    }

    @Test
    void testStatoInizialeECompletamento() {
        // Verifica stato iniziale
        assertEquals(StatoSessione.ASSEGNATA, sessione.getStato(), "Lo stato iniziale deve essere ASSEGNATA");
        assertFalse(sessione.isCompletata(), "La sessione non deve risultare completata appena creata");

        // Esecuzione
        sessione.completaSessione();

        // Verifica aggiornamento stato
        assertEquals(StatoSessione.COMPLETATA, sessione.getStato(), "Lo stato deve essere COMPLETATA");
        assertTrue(sessione.isCompletata(), "La sessione deve ora risultare completata");
    }

    @Test
    void testCalcolaDurataTotaleEffettiva() {
        // Setup Esercizi
        Esercizio squat = new Esercizio("Squat", "4x10", 40, 15);
        squat.registraEsecuzione(40, 20, "Fatica all'ultima serie"); // 20 minuti effettivi

        Esercizio affondi = new Esercizio("Affondi", "3x12", 36, 10);
        affondi.registraEsecuzione(36, 12, "Tutto ok"); // 12 minuti effettivi

        sessione.aggiungiEsercizio(squat);
        sessione.aggiungiEsercizio(affondi);

        // Esecuzione
        int durataEffettiva = sessione.calcolaDurataTotaleEffettiva();

        // Verifica
        assertEquals(32, durataEffettiva, "La durata effettiva deve essere la somma dei tempi impiegati (20 + 12)");
    }
}