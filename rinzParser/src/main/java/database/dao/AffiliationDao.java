package database.dao;

import database.model.Affiliation;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utility.HibernateSessionFactoryUtil;

import java.util.List;

public class AffiliationDao {
    Session session;

    public Affiliation findById(int id) {
        Affiliation affiliation = session.get(Affiliation.class, id);
        return affiliation;
    }

    public void save(Affiliation affiliation) {
        Transaction tx1 = session.beginTransaction();
        session.save(affiliation);
        tx1.commit();
    }

    public void update(Affiliation affiliation) {
        Transaction tx1 = session.beginTransaction();
        session.update(affiliation);
        tx1.commit();
    }

    public void delete(Affiliation affiliation) {
        Transaction tx1 = session.beginTransaction();
        session.delete(affiliation);
        tx1.commit();
    }

    public List<Affiliation> findAll() {
        List<Affiliation> affiliations = (List<Affiliation>) session.createQuery("From database.model.Affiliation").list();
        return affiliations;
    }

    public void openConnection() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public void closeConnection() {
        session.close();
    }
}
