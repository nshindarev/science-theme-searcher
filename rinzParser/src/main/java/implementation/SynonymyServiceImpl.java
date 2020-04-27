package implementation;

import database.model.Author;
import database.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.LengthComparatorService;
import service.SynonymyService;
import service.TranslatorService;

import java.util.List;

public class SynonymyServiceImpl implements SynonymyService {
    private static final Logger logger = LoggerFactory.getLogger(SynonymyServiceImpl.class);

    @Override
    public boolean checkAuthorsEquality(Author firstAuthor, Author secondAuthor) {
        TranslatorService translatorService = new TranslatorServiceImpl();
        LengthComparatorService lengthComparatorService = new LengthComparatorServiceImpl();
        if (lengthComparatorService.samePatronymics(firstAuthor, secondAuthor)) {
            if (lengthComparatorService.getLength(translatorService.translateToLatinString(firstAuthor.getSurname()),
                    translatorService.translateToLatinString(secondAuthor.getSurname())) < 3) {
                if (lengthComparatorService.checkConnections(firstAuthor, secondAuthor) > 0) {
                    logger.debug("Authors: {}, {} probably are the same person", firstAuthor.getSurname(),secondAuthor.getSurname());
                    return true;
                }
                else {
//                    logger.debug("Authors don't have similar co-authors");
                    return false;
                }
            }
            else {
//                logger.debug("Authors have different surnames");
                return false;
            }
        }
        else {
//            logger.debug("Authors have different patronymics");
            return false;
        }
    }

    @Override
    public void authorsJoin(AuthorService authorService, Author author1, Author author2) {
        author1.getIncomingAuthorToAuthors().addAll(author2.getIncomingAuthorToAuthors());
        author1.getOutgoingAuthorToAuthors().addAll(author2.getOutgoingAuthorToAuthors());
        author1.getClusters().addAll(author2.getClusters());
        author1.getLinks().addAll(author2.getLinks());
        author1.getPublications().addAll(author2.getPublications());
        authorService.updateAuthor(author1);
        authorService.deleteAuthor(author2);
    }

    @Override
    public void authorsSearchForSynonyms() {
        AuthorService authorService = new AuthorService();
        authorService.openConnection();
        List<Author> authors = authorService.findAllAuthors();

        for (Author author1: authors) {
            for (Author author2: authors) {
                if (author1.getId()!=author2.getId()) {
                    if (checkAuthorsEquality(author1,author2)) {
                        authorsJoin(authorService, author1, author2);
                    }
                }
            }
        }
    }
}
