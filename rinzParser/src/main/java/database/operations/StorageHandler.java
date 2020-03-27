package database.operations;

import database.model.Author;
import database.service.AuthorService;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StorageHandler  {
    private static final Logger logger = LoggerFactory.getLogger(StorageHandler.class);
    // ============ UPDATE ===========
//    public static void saveAuthors(Collection<Author> authors){
//        try{
//            AuthorService authorService = new AuthorService();
//            authorService.openConnection();
//
//            authors.forEach(authorService::saveAuthor);
//
//            authorService.closeConnection();
//        }
//        catch (ConstraintViolationException ex){
//            logger.error(ex.getMessage());
//        }
//    }
    public static void saveAuthors(Collection<Author> authors){
            AuthorService authorService = new AuthorService();


            for (Author auth: authors){
                try{
                    authorService.openConnection();
                    authorService.saveAuthor(auth);
                }
                catch (ConstraintViolationException ex){
                    logger.error(ex.getMessage());
                }
                finally {
                    authorService.closeConnection();
                }
            }
//            authorService.closeConnection();
    }
    public static void updateRevision (Collection<Author> authors){
        AuthorService authorService = new AuthorService();
        authorService.openConnection();

        //TODO: need to update revision field at each auth from collection
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
}
