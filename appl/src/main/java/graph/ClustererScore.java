package graph;

import com.apporiented.algorithm.clustering.Cluster;
import datamapper.ResearchStarters.Author;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.AsSubgraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.AuthorsDB;

import java.util.*;

public class ClustererScore {

    public Logger logger = LoggerFactory.getLogger(ClustererScore.class);

    // ========= connected component ===============
    AsSubgraph subgraph;

    // ========= distances =========================
    private FloydWarshallShortestPaths shortestPaths;
    private double[][]    A;

    Set<ClusterAuthors> splittedComponent = new HashSet<>();

    public ClustererScore(AsSubgraph subgraph, double[][] distances, int i){

        this.subgraph = subgraph;
        this.A  = distances;
        this.shortestPaths = new FloydWarshallShortestPaths(subgraph);

        this.splittedComponent.addAll(AuthorsDB.authorsInCluster.get(i));
        logger.info(new Double(this.m()).toString());
        logger.info("========== QSCORE ===========");
        logger.info("score =  " +  QScore());

    }

    public double QScore(){
        double res = 1/(2*m());
        double sum = 0;

        for (int i=0; i<A[0].length; i++){
            for(int j=0; j<A[0].length; j++){
                sum  += (A[i][j] - k(i)*k(j)/(2*m()))*sigm(i,j);
            }
        }

        res *= sum;
        return res;
    }


    public double m(){
        double sum = 0;
        for (int i=0; i<A[0].length; i++)
            for(int j=0; j<A[0].length; j++)
                sum  += A[i][j];

        return (0.5 * sum);
    }

    public double k(int i){
        double sum = 0;
        for(int j = 0; j<A[0].length; j++){
            sum += A[i][j];
        }

        return sum;
    }

    public int sigm(int i, int j){
        List<Author> auth = new  LinkedList<Author>(subgraph.vertexSet());
        if (auth.get(i).getCluster() == auth.get(j).getCluster()) return 1;
        else return 0;
    }
}
