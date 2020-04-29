package database.service;

import database.dao.AuthorToAuthorDao;
import database.model.Author;
import database.model.AuthorToAuthor;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AuthorToAuthorService {
    private AuthorToAuthorDao authorToAuthorDao = new AuthorToAuthorDao();

    public AuthorToAuthorService() {
    }

    public AuthorToAuthor findAuthorToAuthor(String id) {
        return authorToAuthorDao.findById(id);
    }

    public void saveAuthorToAuthor(AuthorToAuthor authorToAuthor) {
        try{
            authorToAuthorDao.save(authorToAuthor);
        }
        catch (PersistenceException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void deleteAuthorToAuthor(AuthorToAuthor authorToAuthor) {
        authorToAuthorDao.delete(authorToAuthor);
    }

    public void updateAuthorToAuthor(AuthorToAuthor authorToAuthor) {
        authorToAuthorDao.update(authorToAuthor);
    }

    public List<AuthorToAuthor> findAllAuthorToAuthor (){ return authorToAuthorDao.findAll();}

    public void openConnection() {
        authorToAuthorDao.openConnection();
    }

    public void closeConnection() {
        authorToAuthorDao.closeConnection();
    }

}
