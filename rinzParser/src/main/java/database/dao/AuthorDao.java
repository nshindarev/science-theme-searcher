package database.dao;

import database.model.Author;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateSessionFactoryUtil;

import java.util.List;

public class AuthorDao {
    Session session;
    public Author findById(int id) {
        Author author = session.get(Author.class, id);
        return author;
    }

    public void save(Author author) {
        Transaction tx1 = session.beginTransaction();
        session.save(author);
        tx1.commit();
    }

    public void update(Author author) {
        Transaction tx1 = session.beginTransaction();
        session.update(author);
        tx1.commit();
    }

    public void delete(Author author) {
        Transaction tx1 = session.beginTransaction();
        session.delete(author);
        tx1.commit();
    }

    public List<Author> findAll() {
//        List<Author> authors = (List<Author>)  session.createQuery("FROM science_theme_searcher.author").list();
        List<Author> authors = (List<Author>)  session.createQuery("FROM database.model.Author").list();
        return authors;
    }

    public void openConnection() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public void closeConnection() {
        session.close();
    }
}
