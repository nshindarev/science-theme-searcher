package graph;

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
    private List<ClusterAuthors> clusters;

    Set<ClusterAuthors> splittedComponent = new HashSet<>();

    public ClustererScore(List<ClusterAuthors> clusters, AsSubgraph subgraph, double[][] distances, int i){

        this.subgraph = subgraph;
        this.A  = distances;
        this.shortestPaths = new FloydWarshallShortestPaths(subgraph);

        this.clusters = clusters;

        this.splittedComponent.addAll(AuthorsDB.authorsInCluster.get(i));

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
        if (auth.get(i).getCluster() == auth.get(j).getCluster())

        for(ClusterAuthors clusterAuthors: this.clusters){
            if (clusterAuthors.authors.contains(auth.get(i)) && clusterAuthors.authors.contains(auth.get(j))){
                return 1;
            }
        }
        return 0;
    }
}
