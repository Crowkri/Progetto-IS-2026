package Database;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

public class GestorePersistenza {

    public <T> void salva(T oggetto) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(oggetto);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public <T> boolean salvaTutti(List<T> oggetti) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (T obj : oggetti) {
                em.persist(obj);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public <T> T trovaPerId(Class<T> classe, Long id) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            return em.find(classe, id);
        } finally {
            em.close();
        }
    }

    public <T> List<T> cercaPerCampo(Class<T> classe, String nomeCampo, Object valore) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            // Utilizzo JPQL per creare una query dinamica
            String jpql = "SELECT e FROM " + classe.getSimpleName() + " e WHERE e." + nomeCampo + " = :valore";
            TypedQuery<T> query = em.createQuery(jpql, classe);
            query.setParameter("valore", valore);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public <T> List<T> cercaPerCampi(Class<T> classe, Map<String, Object> campi) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT e FROM " + classe.getSimpleName() + " e WHERE 1=1");
            for (String nomeCampo : campi.keySet()) {
                jpql.append(" AND e.").append(nomeCampo).append(" = :").append(nomeCampo);
            }

            TypedQuery<T> query = em.createQuery(jpql.toString(), classe);

            for (Map.Entry<String, Object> entry : campi.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public <T> T cercaPrimoPerCampi(Class<T> classe, Map<String, Object> campi) {
        List<T> risultati = cercaPerCampi(classe, campi);
        if (risultati != null && !risultati.isEmpty()) {
            return risultati.get(0); // Restituisce il primo elemento trovato
        }
        return null;
    }

    public <T> T aggiorna(T oggetto) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        T oggettoAggiornato = null;
        try {
            tx.begin();
            oggettoAggiornato = em.merge(oggetto); // merge aggiorna il record esistente
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return oggettoAggiornato;
    }

    public <T> boolean elimina(Class<T> classe, Long id) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entitaDaEliminare = em.find(classe, id);
            if (entitaDaEliminare != null) {
                em.remove(entitaDaEliminare);
                tx.commit();
                return true;
            }
            tx.commit(); // Se non trova nulla, chiude comunque la transazione con successo
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}