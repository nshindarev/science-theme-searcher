package dao;

import model.Cluster;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateSessionFactoryUtil;

import java.util.List;

public class ClusterDao {
    public Cluster findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Cluster.class, id);
    }

    public void save(Cluster cluster) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(cluster);
        tx1.commit();
        session.close();
    }

    public void update(Cluster cluster) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(cluster);
        tx1.commit();
        session.close();
    }

    public void delete(Cluster cluster) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(cluster);
        tx1.commit();
        session.close();
    }

    public List<Cluster> findAll() {
        List<Cluster> clusters = (List<Cluster>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From cluster").list();
        return clusters;
    }
}