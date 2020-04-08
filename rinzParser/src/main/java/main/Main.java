package main;

import database.model.Author;
import database.model.AuthorToAuthor;
import database.model.Cluster;
import database.service.AuthorService;
import database.service.AuthorToAuthorService;
import database.service.ClusterService;
import database.service.KeywordService;
import elibrary.auth.LogIntoElibrary;
import elibrary.parser.Parser;
import database.model.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main (String[] args){

        Author test1 = new Author("Test1", "1", "1");
        Author test2 = new Author("Test2", "2", "2");
        AuthorService authorService = new AuthorService();
        authorService.openConnection();
        authorService.closeConnection();
        AuthorToAuthorService authorToAuthorService = new AuthorToAuthorService();
        AuthorToAuthor authorToAuthor = new AuthorToAuthor(test1,test2);
        authorToAuthorService.openConnection();
        authorToAuthorService.saveAuthorToAuthor(authorToAuthor);
        authorToAuthorService.closeConnection();
    }

}
