package main;

import database.model.Author;
import database.model.AuthorToAuthor;
import database.model.Publication;
import database.service.AuthorService;
import implementation.SuggestingServiceImpl;
import implementation.SynonymyServiceImpl;
import implementation.TranslatorServiceImpl;
import service.SuggestingService;
import service.SynonymyService;
import service.TranslatorService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test {
    public static String postgresLogin = "postgres";
    public static String postgresPassword = "N1k1t0s1n4";

    //Example of work
    public static void main(String[] args) {
        SynonymyService synonymyService = new SynonymyServiceImpl();
        TranslatorService translatorService = new TranslatorServiceImpl();
        AuthorService authorService = new AuthorService();
        authorService.openConnection();

        Author author1 = new Author("Shindarev", "N", "A");
        Author author2 = new Author("Шиндарев", "Н", "А");
        Author author3 = new Author("Suleimanov","A","A");
        Author author4 = new Author("Сулейманов","А","А");

        Publication publication1 = new Publication("Test1");
        Publication publication2 = new Publication("Test2");
        Set<Publication> publications1 = new HashSet<>();
        Set<Publication> publications2 = new HashSet<>();
        publications1.add(publication1);
        publications2.add(publication2);
        author3.setPublications(publications1);
        author4.setPublications(publications2);

        AuthorToAuthor authorToAuthor1 = new AuthorToAuthor(author1,author3);
        Set<AuthorToAuthor> set1 = new HashSet<>();
        set1.add(authorToAuthor1);
        AuthorToAuthor authorToAuthor2 = new AuthorToAuthor(author2,author4);
        Set<AuthorToAuthor> set2 = new HashSet<>();
        set2.add(authorToAuthor2);

        author3.setIncomingAuthorToAuthors(set1);
        author4.setIncomingAuthorToAuthors(set2);

        if (synonymyService.checkAuthorsEquality(author3, author4)) {
            synonymyService.authorsJoin(authorService, author3, author4);
        }

    }

    public static void test(String[] args) {
        SuggestingService suggestingService = new SuggestingServiceImpl();
        List<String> publicationList = suggestingService.executeSuggestionQuery("социоинженерные атаки");
        System.out.println(publicationList.size());
    }
}