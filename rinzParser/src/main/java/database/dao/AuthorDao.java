package database.dao;

import database.model.Author;
import database.model.AuthorToAuthor;
import database.model.Cluster;
import database.model.Publication;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateSessionFactoryUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
        for (Publication publication :author.getPublications())
        {
            publication.getAuthors().remove(author);
        }
        for (Cluster cluster :author.getClusters())
        {
            cluster.getAuthors().remove(author);
        }
        author.setIncomingAuthorToAuthors(null);
        author.setOutgoingAuthorToAuthors(null);
        removeLinks(author);
        session.delete(author);
        tx1.commit();
    }

    private void removeLinks(Author author) {
        String url = "jdbc:postgresql://localhost:5432/postgres_sts";
        String user = "postgres";
        String password = "N1k1t0s1n4";
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("DELETE FROM science_theme_searcher.authortoauthor\n" +
                    "\tWHERE id_first ="+author.getId()+"\n" +
                    "\tor id_second = "+author.getId());

        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }
    }

    public List<Author> findAll() {
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
