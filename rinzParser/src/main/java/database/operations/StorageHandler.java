package database.operations;

import database.model.Author;
import database.model.AuthorToAuthor;
import database.model.Publication;
import database.service.AuthorService;
import database.service.AuthorToAuthorService;
import database.service.PublicationService;
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
    public static void savePublication(Publication publication){
        PublicationService ps = new PublicationService();

        ps.openConnection();
        ps.savePublication(publication);
        ps.closeConnection();
    }

    public static void saveCoAuthors(){

        /**
         *  Publications connection
         */
        // publications db
        PublicationService pbService = new PublicationService();
        pbService.openConnection();

        // fill in list with repeated a2a
        List<AuthorToAuthor> a2aList = new LinkedList<>();
        Collection<Publication> allPublications = pbService.findAllPublications();

        allPublications.forEach(publication -> {
            publication.getAuthors().forEach(a1 ->
                    publication.getAuthors().stream().filter(a2 -> !a2.equals(a1)).forEach(a2 ->
                        a2aList.add(new AuthorToAuthor(a1,a2))
                    ));
        });

        // count a2a
        HashMap<AuthorToAuthor, Integer> weightCounter = new HashMap<>();
        for (AuthorToAuthor edge: a2aList){
            if (weightCounter.containsKey(edge)) weightCounter.put(edge, weightCounter.get(edge)+1);
            else weightCounter.put(edge, 1);
        }

        pbService.closeConnection();

        /**
         *  A2A db connection
         */
        AuthorToAuthorService a2aServ = new AuthorToAuthorService();
        a2aServ.openConnection();

        weightCounter.forEach((k,v) ->{
            k.setWeight(v);
            a2aServ.saveAuthorToAuthor(k);
        });

        a2aServ.closeConnection();
    }

    public static void saveAuthors(Collection<Author> authors){
        AuthorService authorService = new AuthorService();

        // to check if DB already contains author
        authorService.openConnection();
        Set<Author> dbAuthorsSnapshot = new HashSet<>(authorService.findAllAuthors());


        for (Author auth: authors){
                try{
                    authorService.openConnection();

                    if (!dbAuthorsSnapshot.contains(auth)){
                        authorService.saveAuthor(auth);
                    }
                    else if (dbAuthorsSnapshot.contains(auth)){

                        Author authDBSaved = authorService.findAuthor(auth.getId());

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
//                finally {
////                    dbAuthorsSnapshot = new HashSet<>(authorService.findAllAuthors());
//                    authorService.closeConnection();
//                }
        }

        authorService.closeConnection();
    }

    public static void updateRevision (Collection<Author> authors){
        AuthorService authorService = new AuthorService();
        authorService.openConnection();

        authors.forEach(authorService::updateAuthor);

        authorService.closeConnection();
    }

    // ====================== READ =======================
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
