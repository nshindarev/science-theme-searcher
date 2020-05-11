package service;

import database.model.Affiliation;
import database.model.Author;
import database.service.AuthorService;

public interface SynonymyService {
    boolean checkAuthorsEquality(Author firstAuthor, Author secondAuthor);

    boolean checkAffiliationsEquality(Affiliation firstAffiliation, Affiliation secondAffiliation);

    void authorsJoin(AuthorService authorService, Author author1, Author author2);

    void authorsSearchForSynonyms();

    void affiliationsSearchForSynonyms();
}
