package storage;

import datamapper.Author;
import datamapper.Publication;

import java.util.*;

public class AuthorsDB {

    private static Map<Author, Set<Publication>> publicationsStorage;
    private static Set<Author>  authorsStorage;

    public static Set<Author> getAuthorsStorage(){
        return AuthorsDB.authorsStorage;
    }
    public static Map<Author, Set<Publication>> getPublicationsStorage() {
        return AuthorsDB.publicationsStorage;
    }


    public static void addToAuthorsStorage(Author author){
        AuthorsDB.authorsStorage.add(author);

        if(!publicationsStorage.containsKey(author)){
            publicationsStorage.put(author, new HashSet<>());
        }
    }

    public static void initPublicationsStorage(){
        AuthorsDB.publicationsStorage = new HashMap<>();
    }
    public static void initAuthorsStorage(){
        AuthorsDB.authorsStorage = new HashSet<>();
    }
}
