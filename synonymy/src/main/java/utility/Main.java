package utility;

import database.model.Author;
import database.model.AuthorToAuthor;
import implementation.SynonymyServiceImpl;
import implementation.TranslatorServiceImpl;
import org.apache.log4j.BasicConfigurator;
import service.SynonymyService;
import service.TranslatorService;

import java.util.HashSet;
import java.util.Set;

public class Main {

    //Example of work
    public static void main(String[] args) {
        BasicConfigurator.configure();
        SynonymyService synonymyService = new SynonymyServiceImpl();
        TranslatorService translatorService = new TranslatorServiceImpl();

        Author author1 = new Author("Shindarev", "N", "A");
        Author author2 = new Author("Slezkin", "N", "E");
        Author author3 = new Author(translatorService.translateToLatinString("Suleimanov"),
                translatorService.translateToLatinString("A"),
                translatorService.translateToLatinString("A"));
        Author author4 = new Author(translatorService.translateToLatinString("Сулейманов"),
                translatorService.translateToLatinString("А"),
                translatorService.translateToLatinString("А"));

        AuthorToAuthor authorToAuthor1 = new AuthorToAuthor(author1,author3);
        Set<AuthorToAuthor> set1 = new HashSet<>();
        set1.add(authorToAuthor1);
        AuthorToAuthor authorToAuthor2 = new AuthorToAuthor(author1,author4);
        Set<AuthorToAuthor> set2 = new HashSet<>();
        set2.add(authorToAuthor2);

        author3.setIncomingAuthorToAuthors(set1);
        author4.setIncomingAuthorToAuthors(set2);

        synonymyService.checkAuthorsEquality(author2, author3);
        synonymyService.checkAuthorsEquality(author3, author4);

    }
}