package database.operations;

import database.model.Author;
import database.service.AuthorService;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

public class StorageHandler  {
    private static final Logger logger = LoggerFactory.getLogger(StorageHandler.class);


    // ===================== UPDATE ===========================
    public static void saveAuthors(Collection<Author> authors){
        AuthorService authorService = new AuthorService();

        // to check if DB already contains author
        authorService.openConnection();
        Set<Author> dbAuthorsSnapshot = new HashSet<>(authorService.findAllAuthors());
        authorService.closeConnection();

        //need this to update authors
        ArrayList<Author> authorList = new ArrayList(dbAuthorsSnapshot);

        for (Author auth: authors){
                try{
                    authorService.openConnection();

                    if (!dbAuthorsSnapshot.contains(auth)){
                        authorService.saveAuthor(auth);
                    }
                    else if (dbAuthorsSnapshot.contains(auth)){

                        int authBoSavedIndex = authorList.indexOf(auth);
                        Author authDBSaved = authorList.get(authBoSavedIndex);

                        // join publications
                        auth.join(authDBSaved);


                        authorService.updateAuthor(auth);
                    }
                }
                catch (ConstraintViolationException ex){
                    logger.error(ex.getMessage());
                }
                catch (DataException ex){
                    logger.error(ex.getMessage());
                }
                finally {
                    dbAuthorsSnapshot = new HashSet<>(authorService.findAllAuthors());
                    authorService.closeConnection();
                }
        }
    }
    public static void updateRevision (Collection<Author> authors){
        AuthorService authorService = new AuthorService();
        authorService.openConnection();

        authors.forEach(authorService::updateAuthor);

        authorService.closeConnection();
    }

    //   ==================== READ =======================
    public static Collection<Author> getAuthorsWithoutRevision(){
        AuthorService authorService = new AuthorService();
        authorService.openConnection();

        Set<Author> res = authorService.findAllAuthors()
                .stream()
                .filter(it -> it.getRevision()==0)
                .collect(Collectors.toSet());

        authorService.closeConnection();
        return res;
    }
    public static DefaultDirectedGraph getAuthorsGraph (){
        DefaultDirectedGraph<Author, DefaultEdge> graph =
                new DefaultDirectedGraph<>(DefaultEdge.class);
        AuthorService authorService = new AuthorService();
        authorService.openConnection();

       authorService.findAllAuthors().forEach(auth -> {
           graph.addVertex(auth);
           auth.getPublications().forEach(publication -> {
              publication.getAuthors().forEach(coAuthor ->{
                  graph.addVertex(coAuthor);
                  if(!auth.equals(coAuthor)){
                      graph.addEdge(auth, coAuthor);
                      graph.addEdge(coAuthor, auth);
                  }
              });
           });
       });

       authorService.closeConnection();
       return graph;
    }
}
