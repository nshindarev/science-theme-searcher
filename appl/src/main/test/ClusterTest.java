import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import datamapper.ResearchStarters.Author;
import graph.Clusterizer;
import io.Serializer;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.AuthorsDB;

import java.util.List;

import static io.Serializer.deserializeGraph;

public class ClusterTest {

    private static final Logger logger = LoggerFactory.getLogger(ClusterTest.class);

    DefaultUndirectedGraph<Author, DefaultEdge> graph;

    @Before
    public void testFileReader(){
        this.graph = deserializeGraph();
    }

    @Test
    public void testFloydWarshall(){
        FloydWarshallShortestPaths shortestPaths = new FloydWarshallShortestPaths(this.graph);
        for(Author author: graph.vertexSet()) {
                for (Author author1 : graph.vertexSet()) {
                    try{
                        logger.info(author.toString() + " ---> " + author1.toString() + " = " + shortestPaths.getPath(author, author1).getLength());
                    }
                    catch (NullPointerException ex){
                        logger.error(author.toString() + " " + author1.toString());
                    }
                }
        }
        double [][] distanceMatrix = new double[][]{};
        List<Author> firstLevel = Clusterizer.getAllAuthAsList();

        for(int i=0; i< AuthorsDB.getAuthorsStorage().size(); i++ ){

        }

    }

    @Test
    public void testClusterizerInit(){
        Clusterizer clusterizer = new Clusterizer(true);

    }
}
