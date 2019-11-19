package dao;

import model.Link;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateSessionFactoryUtil;

import java.util.List;

public class LinkDao {
    public Link findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Link.class, id);
    }

    public void save(Link link) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(link);
        tx1.commit();
        session.close();
    }

    public void update(Link link) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(link);
        tx1.commit();
        session.close();
    }

    public void delete(Link link) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(link);
        tx1.commit();
        session.close();
    }

    public List<Link> findAll() {
        List<Link> links = (List<Link>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From link").list();
        return links;
    }
}