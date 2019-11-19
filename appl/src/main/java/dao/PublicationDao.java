package dao;

import model.Publication;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateSessionFactoryUtil;

import java.util.List;

public class PublicationDao {
    public Publication findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Publication.class, id);
    }

    public void save(Publication publication) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(publication);
        tx1.commit();
        session.close();
    }

    public void update(Publication publication) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(publication);
        tx1.commit();
        session.close();
    }

    public void delete(Publication publication) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(publication);
        tx1.commit();
        session.close();
    }

    public List<Publication> findAll() {
        List<Publication> publications = (List<Publication>) HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From publication").list();
        return publications;
    }
}
