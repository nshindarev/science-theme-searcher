package storage;

import datamapper.ResearchStarters.Author;
import datamapper.Publication;
import graph.ClusterAuthors;

import java.io.Serializable;
import java.util.*;

public class AuthorsDB implements Serializable {

    private static Map<Author, Set<Publication>> publicationsStorage;
    /**
     *  case Theme research: contains all authors of publications in current research topic
     *  case Author search:  contains all co-authors
     */
    private static Set<Author>  authorsStorage;

    /**
     * Key: number of connected component
     * Value: Set of cluster
     */
    public static Map<Integer,Set<ClusterAuthors>> authorsInCluster = new HashMap<>();


    public static Set<Author> getAuthorsStorage(){
        return AuthorsDB.authorsStorage;
    }
    public static Map<Author, Set<Publication>> getPublicationsStorage() {
        return AuthorsDB.publicationsStorage;
    }


    public static void removeFromAuthStorage(Author author){
        authorsStorage.remove(author);
    }
    public static void replaceInAuthStorage (Author author){
        if (authorsStorage.contains(author)){
            authorsStorage.remove(author);
            authorsStorage.add(author);
        }
        else for (Author subAuthor: authorsStorage){
            if(subAuthor.coAuthors.contains(author)){
                subAuthor.coAuthors.remove(author);
                subAuthor.coAuthors.add(author);
            }
        }
    }

    public static void addToAuthorsStorage (Set<Author> authors){
        for(Author auth: authors){
            addToAuthorsStorage(auth);
        }
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
