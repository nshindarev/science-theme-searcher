import database.model.Author;
import database.operations.StorageHandler;
import database.service.AuthorService;
import graph.ui.GraphVisualizer;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(StorageHandlerTest.class);
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

    @Test
    public void testSerializedData(){
        AuthorService authorService = new AuthorService();
        authorService.openConnection();

        authorService.findAllAuthors().forEach(it ->{
            logger.debug(it.toString());
            it.getPublications()
                    .forEach(publication -> logger.debug(publication.toString()));
        });

        authorService.closeConnection();
    }

    @Test
    public void testMappingIntoGraph(){
        DefaultDirectedGraph authorsGraph = StorageHandler.getAuthorsGraph();
//        authorsGraph.vertexSet().forEach(it ->{
//            logger.debug( "DEGREE "  + authorsGraph.degreeOf(it) + " <===> "+ it.toString());
//        });



        GraphVisualizer visualizer = new GraphVisualizer(authorsGraph);
        visualizer.visualize();
    }
}
