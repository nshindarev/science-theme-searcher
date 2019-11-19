package dao;

import model.Keyword;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateSessionFactoryUtil;

import java.util.List;

public class KeywordDao {
    public Keyword findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Keyword.class, id);
    }

    public void save(Keyword keyword) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(keyword);
        tx1.commit();
        session.close();
    }

    public void update(Keyword keyword) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(keyword);
        tx1.commit();
        session.close();
    }

    public void delete(Keyword keyword) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(keyword);
        tx1.commit();
        session.close();
    }

    public List<Keyword> findAll() {
        List<Keyword> keywords = (List<Keyword>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From keyword").list();
        return keywords;
    }
}