package database.dao;

import database.model.Keyword;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateSessionFactoryUtil;

import java.util.List;

public class KeywordDao {
    Session session;

    public Keyword findById(int id) {
        Keyword keyword = session.get(Keyword.class, id);
        return keyword;
    }

    public void save(Keyword keyword) {
        Transaction tx1 = session.beginTransaction();
        session.save(keyword);
        tx1.commit();
    }

    public void update(Keyword keyword) {
        Transaction tx1 = session.beginTransaction();
        session.update(keyword);
        tx1.commit();
    }

    public void delete(Keyword keyword) {
        Transaction tx1 = session.beginTransaction();
        session.delete(keyword);
        tx1.commit();
    }

    public List<Keyword> findAll() {
        List<Keyword> keywords = (List<Keyword>)  session.createQuery("From science_theme_searcher.keyword").list();
        return keywords;
    }

    public void openConnection() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public void closeConnection() {
        session.close();
    }
}