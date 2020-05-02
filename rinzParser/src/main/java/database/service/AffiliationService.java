package database.service;

import database.dao.AffiliationDao;
import database.model.Affiliation;

import java.util.List;

public class AffiliationService {
    private AffiliationDao affiliationDao = new AffiliationDao();

    public AffiliationService() {
    }

    public Affiliation findAffiliation(int id) {
        return affiliationDao.findById(id);
    }

    public void saveAffiliation(Affiliation affiliation) {
        affiliationDao.save(affiliation);
    }

    public void deleteAffiliation(Affiliation affiliation) {
        affiliationDao.delete(affiliation);
    }

    public void updateAffiliation(Affiliation affiliation) {
        affiliationDao.update(affiliation);
    }

    public List<Affiliation> findAllAffiliations() {
        return affiliationDao.findAll();
    }

    public void openConnection() {
        affiliationDao.openConnection();
    }

    public void closeConnection() {
        affiliationDao.closeConnection();
    }
}
