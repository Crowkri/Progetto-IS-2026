package Database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {

    // Singleton
    private static JpaUtil istance;

    private EntityManagerFactory emf;

    private JpaUtil() {
        // "GestioneAllenamentiPU" è il nome che useremo nel file di configurazione
        this.emf = Persistence.createEntityManagerFactory("GestioneAllenamentiPU");
    }

    public static JpaUtil getInstance() {
        if (istance == null) {
            istance = new JpaUtil();
        }
        return istance;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void chiudi() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}