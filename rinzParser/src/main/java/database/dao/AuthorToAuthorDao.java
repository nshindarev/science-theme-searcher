package database.dao;

import database.model.AuthorToAuthor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateSessionFactoryUtil;

import java.util.List;

public class AuthorToAuthorDao {
    Session session;
    public AuthorToAuthor findById(int id) {
        AuthorToAuthor authorToAuthor = session.get(AuthorToAuthor.class, id);
        return authorToAuthor;
    }

    public void save(AuthorToAuthor authorToAuthor) {
        Transaction tx1 = session.beginTransaction();
        session.save(authorToAuthor);
        tx1.commit();
    }

    public void update(AuthorToAuthor authorToAuthor) {
        Transaction tx1 = session.beginTransaction();
        session.update(authorToAuthor);
        tx1.commit();
    }

    public void delete(AuthorToAuthor authorToAuthor) {
        Transaction tx1 = session.beginTransaction();
        session.delete(authorToAuthor);
        tx1.commit();
    }

    public List<AuthorToAuthor> findAll() {
        List<AuthorToAuthor> authorToAuthors = (List<AuthorToAuthor>)  session.createQuery("FROM database.model.AuthorToAuthor").list();
        return authorToAuthors;
    }

    public void openConnection() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public void closeConnection() {
        session.close();
    }
}
