package Database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {

    // L'unica istanza statica della classe (Singleton)
    private static JpaUtil istance;

    private EntityManagerFactory emf;

    // Costruttore privato: impedisce l'istanziazione dall'esterno
    private JpaUtil() {
        // "GestioneAllenamentiPU" è il nome che useremo nel file di configurazione
        this.emf = Persistence.createEntityManagerFactory("GestioneAllenamentiPU");
    }

    // Metodo pubblico per ottenere l'istanza Singleton
    public static JpaUtil getInstance() {
        if (istance == null) {
            istance = new JpaUtil();
        }
        return istance;
    }

    // Fornisce un nuovo EntityManager per le transazioni
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Chiude la connessione quando l'applicazione si spegne
    public void chiudi() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}