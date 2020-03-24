package database.dao;

import database.model.Cluster;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateSessionFactoryUtil;

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

    public void openConnection() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public void closeConnection() {
        session.close();
    }
}