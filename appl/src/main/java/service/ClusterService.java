package service;

import dao.ClusterDao;
import model.Cluster;
import util.HibernateSessionFactoryUtil;

import java.util.List;

public class ClusterService {
    private ClusterDao clusterDao = new ClusterDao();

    public ClusterService() {
    }

    public Cluster findCluster(int id) {
        return clusterDao.findById(id);
    }

    public void saveCluster(Cluster cluster) {
        clusterDao.save(cluster);
    }

    public void deleteCluster(Cluster cluster) {
        clusterDao.delete(cluster);
    }

    public void updateCluster(Cluster cluster) {
        clusterDao.update(cluster);
    }

    public List<Cluster> findAllClusters() {
        return clusterDao.findAll();
    }

    public void openConnection() {
        clusterDao.openConnection();
    }

    public void closeConnection() {
        clusterDao.closeConnection();
    }
}
