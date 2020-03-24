package database.service;

import database.dao.PublicationDao;
import database.model.Publication;

import java.util.List;

public class PublicationService {
    private PublicationDao publicationDao = new PublicationDao();

    public PublicationService() {
    }

    public Publication findPublication(int id) {
        return publicationDao.findById(id);
    }

    public void savePublication(Publication publication) {
        publicationDao.save(publication);
    }

    public void deletePublication(Publication publication) {
        publicationDao.delete(publication);
    }

    public void updatePublication(Publication publication) {
        publicationDao.update(publication);
    }

    public List<Publication> findAllPublications() {
        return publicationDao.findAll();
    }

    public void openConnection() {
        publicationDao.openConnection();
    }

    public void closeConnection() {
        publicationDao.closeConnection();
    }
}
