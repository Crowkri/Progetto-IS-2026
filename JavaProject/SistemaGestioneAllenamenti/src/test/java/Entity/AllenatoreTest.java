package Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class AllenatoreTest {

    private Allenatore allenatore;
    private Atleta atleta;

    @BeforeEach
    void setUp() {
        // Inizializziamo gli oggetti prima di ogni test
        allenatore = new Allenatore("COD123", "Mario", "Rossi", "mario@email.com", "pass123", "Pesistica");
        atleta = new Atleta("Luigi", "Verdi", "luigi@email.com", "pass456", "Pesistica");
    }

    @Test
    void testAggiungiAtleta_Successo() {
        // Esecuzione
        boolean risultato = allenatore.aggiungiAtleta(atleta);

        // Verifica
        assertTrue(risultato, "L'atleta dovrebbe essere aggiunto con successo");
        assertTrue(allenatore.haAtletaAssociato(atleta), "L'allenatore deve avere l'atleta nella sua lista");
        assertEquals(allenatore, atleta.getAllenatoreAssociato(), "L'atleta deve avere il riferimento all'allenatore corretto");
    }

    @Test
    void testAggiungiAtleta_GiaPresente() {
        // Setup iniziale: aggiungiamo l'atleta una prima volta
        allenatore.aggiungiAtleta(atleta);

        // Esecuzione: proviamo ad aggiungerlo di nuovo
        boolean risultatoDuplicato = allenatore.aggiungiAtleta(atleta);

        // Verifica
        assertFalse(risultatoDuplicato, "Non dovrebbe essere possibile aggiungere un atleta già presente");
        assertEquals(1, allenatore.getAtletiAssociati().size(), "La lista degli atleti non deve contenere duplicati");
    }

    @Test
    void testRimuoviAtleta() {
        allenatore.aggiungiAtleta(atleta);

        boolean risultato = allenatore.rimuoviAtleta(atleta);

        assertTrue(risultato, "L'atleta dovrebbe essere rimosso con successo");
        assertFalse(allenatore.haAtletaAssociato(atleta), "L'atleta non deve più essere nella lista");
        assertNull(atleta.getAllenatoreAssociato(), "Il riferimento all'allenatore nell'atleta deve essere null (disassociato)");
    }
}