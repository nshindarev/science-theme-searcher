package database.service;

import database.dao.AuthorToAuthorDao;
import database.model.Author;
import database.model.AuthorToAuthor;

import java.util.List;
import java.util.Objects;

public class AuthorToAuthorService {
    private AuthorToAuthorDao authorToAuthorDao = new AuthorToAuthorDao();

    public AuthorToAuthorService() {
    }

    public AuthorToAuthor findAuthorToAuthor(int id) {
        return authorToAuthorDao.findById(id);
    }

    public void saveAuthorToAuthor(AuthorToAuthor authorToAuthor) {
        authorToAuthorDao.save(authorToAuthor);
    }

    public void deleteAuthorToAuthor(AuthorToAuthor authorToAuthor) {
        authorToAuthorDao.delete(authorToAuthor);
    }

    public void updateAuthorToAuthor(AuthorToAuthor authorToAuthor) {
        authorToAuthorDao.update(authorToAuthor);
    }

    public void openConnection() {
        authorToAuthorDao.openConnection();
    }

    public void closeConnection() {
        authorToAuthorDao.closeConnection();
    }

}
