package db;

import database.model.Author;
import database.service.AuthorService;

import java.util.Collection;

public class StorageHandler  {
    public static void clearAuthors(){
        AuthorService authorService = new AuthorService();
        authorService.findAllAuthors().forEach(it -> authorService.deleteAuthor(it));
    }
    public static void saveAuthors(Collection<Author> authors){

           AuthorService authorService = new AuthorService();
           authors.forEach(auth ->
                   authorService.saveAuthor(auth));

    }
}
