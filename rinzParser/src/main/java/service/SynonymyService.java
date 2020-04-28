package service;

import database.model.Author;
import database.service.AuthorService;

public interface SynonymyService {
    boolean checkAuthorsEquality(Author firstAuthor, Author secondAuthor);

    void authorsJoin(AuthorService authorService, Author author1, Author author2);

    void authorsSearchForSynonyms();
}
