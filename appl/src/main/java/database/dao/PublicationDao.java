package database.dao;

import database.model.Publication;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateSessionFactoryUtil;

import java.util.List;

public class PublicationDao {
    Session session;

    public Publication findById(int id) {
        Publication publication = session.get(Publication.class, id);
        return publication;
    }

    public void save(Publication publication) {
        Transaction tx1 = session.beginTransaction();
        session.save(publication);
        tx1.commit();
    }

    public void update(Publication publication) {
        Transaction tx1 = session.beginTransaction();
        session.update(publication);
        tx1.commit();
    }

    public void delete(Publication publication) {
        Transaction tx1 = session.beginTransaction();
        session.delete(publication);
        tx1.commit();
    }

    public List<Publication> findAll() {
        List<Publication> publications = (List<Publication>) session.createQuery("From science_theme_searcher.publication").list();
        return publications;
    }

    public void openConnection() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public void closeConnection() {
        session.close();
    }
}
