package implementation;

import database.model.Affiliation;
import database.model.Author;
import database.service.AffiliationService;
import database.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.LengthComparatorService;
import service.SynonymyService;
import service.TranslatorService;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class SynonymyServiceImpl implements SynonymyService {
    private static final Logger logger = LoggerFactory.getLogger(SynonymyServiceImpl.class);
    static HashSet<Author> deleted = new HashSet<>();
    static HashSet<Affiliation> deletedAffiliations = new HashSet<>();
    TranslatorService translatorService = new TranslatorServiceImpl();
    LengthComparatorService lengthComparatorService = new LengthComparatorServiceImpl();

    @Override
    public boolean checkAuthorsEquality(Author firstAuthor, Author secondAuthor) {
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
    public boolean checkAffiliationsEquality(Affiliation firstAffiliation, Affiliation secondAffiliation) {
        String firstName = firstAffiliation.getName().replaceAll("\\s+", "").replaceAll("-", "").toLowerCase();
        String secondName = secondAffiliation.getName().replaceAll("\\s+", "").replaceAll("-", "").toLowerCase();
        if (firstName.length()>25 && secondName.length()>25 && firstName.substring(0,25).equals(secondName.substring(0,25))) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Одинаковы ли аффиляции? (y/n) \n  1)  "+firstAffiliation.getName()+"\n  2)  "+secondAffiliation.getName());
            String phrase = sc.nextLine();
            if (phrase.equals("y")) {
                return true;
            }
            else {
                return false;
            }
        }  else {
            if (firstName.equals(secondName)) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void authorsJoin(AuthorService authorService, Author author1, Author author2) {
        author1.getIncomingAuthorToAuthors().addAll(author2.getIncomingAuthorToAuthors());
        author1.getOutgoingAuthorToAuthors().addAll(author2.getOutgoingAuthorToAuthors());
        author1.getClusters().addAll(author2.getClusters());
        author1.getLinks().addAll(author2.getLinks());
        author1.getPublications().addAll(author2.getPublications());
        author1.getAffiliations().addAll(author2.getAffiliations());
        authorService.updateAuthor(author1);
        deleted.add(author2);
    }

    public void affiliationJoin(AffiliationService affiliationService, Affiliation affiliation1, Affiliation affiliation2) {
        affiliation1.getAuthors().addAll(affiliation2.getAuthors());
        affiliationService.updateAffiliation(affiliation1);
        deletedAffiliations.add(affiliation2);
    }

    private void deleteAuthors(AuthorService authorService) {
        for (Author author:deleted) {
            authorService.deleteAuthor(author);
        }
    }

    private void deleteAffiliations(AffiliationService affiliationService) {
        for (Affiliation affiliation:deletedAffiliations) {
            affiliationService.deleteAffiliation(affiliation);
        }
    }

    @Override
    public void authorsSearchForSynonyms() {
        AuthorService authorService = new AuthorService();
        authorService.openConnection();
        List<Author> authors = authorService.findAllAuthors();
        logger.debug("Start searching similarities");
        for (Author author1: authors) {
            for (Author author2 : authors) {
                if (author1.getId() != author2.getId() && !deleted.contains(author1)) {
                    if (checkAuthorsEquality(author1, author2)) {
                        authorsJoin(authorService, author1, author2);
                    }
                }
            }
        }
        logger.debug("Finished searching similarities, similarities found: {}",deleted.size());
        deleteAuthors(authorService);
        authorService.closeConnection();
    }

    @Override
    public void affiliationsSearchForSynonyms() {
        AffiliationService affiliationService = new AffiliationService();
        affiliationService.openConnection();
        List<Affiliation> affiliations = affiliationService.findAllAffiliations();
        logger.debug("Start searching similarities");
        for (Affiliation affiliation1: affiliations) {
            for (Affiliation affiliation2 : affiliations) {
                if (affiliation1.getId() != affiliation2.getId() && !deletedAffiliations.contains(affiliation1)) {
                    if (checkAffiliationsEquality(affiliation1, affiliation2)) {
                        affiliationJoin(affiliationService, affiliation1, affiliation2);
                    }
                }
            }
        }
        logger.debug("Finished searching similarities, similarities found: {}",deletedAffiliations.size());
        deleteAffiliations(affiliationService);
        affiliationService.closeConnection();
    }
}
