import database.model.Author;
import database.service.AuthorService;
import org.junit.Test;

public class StorageHandlerTest {
    @Test
    public void testRevisionUpdate(){
        Author a = new Author("Аааааа","а","а");
        Author a2 = a;
        a2.setRevision(1);


        AuthorService authorService = new AuthorService();

        authorService.openConnection();
        authorService.saveAuthor(a);
        authorService.saveAuthor(a2);
        authorService.closeConnection();
    }
}
