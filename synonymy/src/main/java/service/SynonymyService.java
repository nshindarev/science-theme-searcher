package service;

import database.model.Author;

public interface SynonymyService {
    boolean checkAuthorsEquality(Author firstAuthor, Author secondAuthor);
}
