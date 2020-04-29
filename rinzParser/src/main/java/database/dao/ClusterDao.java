package database.dao;

import database.model.Cluster;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateSessionFactoryUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class ClusterDao {
    Session session;
    
    public Cluster findById(int id) {
        Cluster cluster = session.get(Cluster.class, id);
        return cluster;
    }

    public void save(Cluster cluster) {
        Transaction tx1 = session.beginTransaction();
        session.save(cluster);
        tx1.commit();
    }

    public void update(Cluster cluster) {
        Transaction tx1 = session.beginTransaction();
        session.update(cluster);
        tx1.commit();
    }

    public void delete(Cluster cluster) {
        Transaction tx1 = session.beginTransaction();
        session.delete(cluster);
        tx1.commit();
    }

    public List<Cluster> findAll() {
        List<Cluster> clusters = (List<Cluster>)  session.createQuery("From science_theme_searcher.cluster").list();
        return clusters;
    }

    public void clearClusterData(){
        removeClusterToAuthor();
        removeClusters();
    }
    private void removeClusterToAuthor() {
        String url = "jdbc:postgresql://localhost:5432/postgres_sts";
        String user = "postgres";
        String password = "N1k1t0s1n4";
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("DELETE FROM science_theme_searcher.ClusterToAuthor\n");

        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }
    }

    private void removeClusters() {
        String url = "jdbc:postgresql://localhost:5432/postgres_sts";
        String user = "postgres";
        String password = "postgres";
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("DELETE FROM science_theme_searcher.Cluster\n");

        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }
    }

    public void openConnection() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public void closeConnection() {
        session.close();
    }
}