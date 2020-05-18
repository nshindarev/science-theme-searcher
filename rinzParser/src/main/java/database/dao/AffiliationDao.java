package database.dao;

import database.model.Affiliation;
import database.model.Author;
import database.model.Cluster;
import database.model.Publication;
import main.Parameters;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utility.HibernateSessionFactoryUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
        System.out.println("Deleted affiliation: "+affiliation.toString());
        Transaction tx1 = session.beginTransaction();
        for (Author author : affiliation.getAuthors())
        {
            author.getAffiliations().remove(affiliation);
        }
        affiliation.setAuthors(null);
        tx1.commit();
        removeAuthors(affiliation);
        removeAffiliation(affiliation);
    }

    private void removeAuthors(Affiliation affiliation) {
        String url = "jdbc:postgresql://localhost:5432/postgres_sts";
        String user = Parameters.postgresLogin;
        String password = Parameters.postgresPassword;
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("DELETE FROM science_theme_searcher.authortoaffiliation\n" +
                    "\tWHERE id_affiliation = "+affiliation.getId());

        } catch (Exception ex) {
            //System.out.println(ex.getStackTrace());
        }
    }

    private void removeAffiliation(Affiliation affiliation) {
        String url = "jdbc:postgresql://localhost:5432/postgres_sts";
        String user = Parameters.postgresLogin;
        String password = Parameters.postgresPassword;
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("DELETE FROM science_theme_searcher.affiliation\n" +
                    "\tWHERE id = "+affiliation.getId());

        } catch (Exception ex) {
            //System.out.println(ex.getStackTrace());
        }
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
