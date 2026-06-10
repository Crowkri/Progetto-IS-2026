import Control.GestoreUtenti;
import Database.GestorePersistenza;
import Database.JpaUtil;
import Entity.Atleta;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("Avvio del sistema e connessione al database in corso...");

        // 1. Inizializziamo il database e il gestore
        GestorePersistenza db = new GestorePersistenza();
        GestoreUtenti gestoreUtenti = new GestoreUtenti(db);

        try {
            System.out.println("Tentativo di registrazione di un nuovo atleta...");

            // 2. Proviamo a inserire un record di test
            Atleta nuovoAtleta = gestoreUtenti.registraAtleta(
                "Mario",
                "Rossi",
                "mario.rossi@email.it",
                "passwordSegreta123",
                "Ciclismo"
            );

            // Se arriviamo qui, JPA ha fatto tutta la magia!
            System.out.println("\n=============================================");
            System.out.println("SUCCESSO! L'applicazione si è collegata al DB.");
            System.out.println("Atleta registrato correttamente!");
            System.out.println("ID assegnato automaticamente da MySQL: " + nuovoAtleta.getIdAtleta());
            System.out.println("=============================================\n");

        } catch (Exception e) {
            System.err.println("ERRORE DURANTE IL COLLEGAMENTO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 3. Chiudiamo le connessioni in modo pulito
            JpaUtil.getInstance().chiudi();
            System.out.println("Connessione chiusa.");
        }
    }
}