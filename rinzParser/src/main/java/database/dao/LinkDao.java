package database.dao;

import database.model.Link;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utility.HibernateSessionFactoryUtil;

import java.util.List;

public class LinkDao {
    Session session;

    public Link findById(int id) {
        Link link = session.get(Link.class, id);
        return link;
    }

    public void save(Link link) {
        Transaction tx1 = session.beginTransaction();
        session.save(link);
        tx1.commit();
    }

    public void update(Link link) {
        Transaction tx1 = session.beginTransaction();
        session.update(link);
        tx1.commit();
    }

    public void delete(Link link) {
        Transaction tx1 = session.beginTransaction();
        session.delete(link);
        tx1.commit();
    }

    public List<Link> findAll() {
        List<Link> links = (List<Link>)  session.createQuery("From database.model.Link").list();
        return links;
    }

    public void openConnection() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public void closeConnection() {
        session.close();
    }
}