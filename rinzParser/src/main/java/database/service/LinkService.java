package database.service;

import database.dao.LinkDao;
import database.model.Link;

import java.util.List;

public class LinkService {
    private LinkDao linkDao = new LinkDao();

    public LinkService() {
    }

    public Link findLink(int id) {
        return linkDao.findById(id);
    }

    public void saveLink(Link link) {
        linkDao.save(link);
    }

    public void deleteLink(Link link) {
        linkDao.delete(link);
    }

    public void updateLink(Link link) {
        linkDao.update(link);
    }

    public List<Link> findAllLinks() {
        return linkDao.findAll();
    }

    public void openConnection() {
        linkDao.openConnection();
    }

    public void closeConnection() {
        linkDao.closeConnection();
    }
}
